/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.config.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.switchyard.common.io.pull.PropertiesPuller;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.common.type.Classes;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.config.ConfigMessages;
import org.switchyard.config.Configuration;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * Holds meta-information used to create, marshall and validate Models.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Descriptor {

    /** The default location to looks for a Descriptor's Properties.  ALL properties found with this name via {@link org.switchyard.common.type.Classes#getResources(String, Class)} will be combined. */
    public static final String DEFAULT_PROPERTIES = "/org/switchyard/config/model/descriptor.properties";

    /** The "namespace" property. */
    public static final String NAMESPACE = "namespace";

    /** The "schema" property. */
    public static final String SCHEMA = "schema";

    /** The "noNamespaceSchema" property. */
    public static final String NO_NAMESPACE_SCHEMA = "noNamespaceSchema";

    /** The "location" property. */
    public static final String LOCATION = "location";

    /** The "marshaller" property. */
    public static final String MARSHALLER = "marshaller";

    private Map<String,String> _all_properties_map = new TreeMap<String,String>();
    private Map<String,Map<String,String>> _prefix_config_map = new HashMap<String,Map<String,String>>();
    private Map<String,String> _namespace_prefix_map = new HashMap<String,String>();
    private Map<String, String> _nonamespace_location_map = new HashMap<String, String>();

    private Map<Set<String>,Schema> _namespaces_schema_map = new HashMap<Set<String>,Schema>();
    private Map<String,Marshaller> _namespace_marshaller_map = new HashMap<String,Marshaller>();

    /**
     * Constructs a new Descriptor based on discovered default properties.
     */
    public Descriptor() {
        addDefaultProperties();
    }

    /**
     * Constructs a new Descriptor based on the specified properties.
     * @param props the Properties
     */
    public Descriptor(Properties props) {
        addProperties(props);
    }

    /**
     * Adds discovered default properties.
     */
    public void addDefaultProperties() {
        addDefaultProperties(Descriptor.class);
    }

    /**
     * Adds discovered default properties using the classloader of the specified class.
     * @param caller the class whose classloader should be used to look up the default properties
     */
    public void addDefaultProperties(Class<?> caller) {
        addDefaultProperties(caller != null ? caller.getClassLoader() : null);
    }

    /**
     * Adds discovered default properties using the specified classloader.
     * @param loader the classloader to use to look up the default properties
     */
    public void addDefaultProperties(ClassLoader loader) {
        Properties props = new Properties();
        PropertiesPuller props_puller = new PropertiesPuller();
        try {
            List<URL> urls = Classes.getResources(DEFAULT_PROPERTIES, loader);
            for (URL url : urls) {
                Properties url_props = props_puller.pull(url);
                Enumeration<?> pn_enum = url_props.propertyNames();
                while (pn_enum.hasMoreElements()) {
                    String pn = (String)pn_enum.nextElement();
                    props.setProperty(pn, url_props.getProperty(pn));
                }
            }
        } catch (IOException ioe) {
            // should never happen
            throw new RuntimeException(ioe);
        }
        addProperties(props);
    }

    private void addProperties(Properties props) {
        Enumeration<?> e = props.propertyNames();
        while (e.hasMoreElements()) {
            String prop_name = (String)e.nextElement();
            String prop_value = props.getProperty(prop_name);
            if (prop_value != null) {
                _all_properties_map.put(prop_name, prop_value);
                int pos = prop_name.lastIndexOf('.');
                String prop_prefix = prop_name.substring(0, pos).trim();
                String prop_suffix = prop_name.substring(pos+1, prop_name.length()).trim();
                Map<String,String> config = _prefix_config_map.get(prop_prefix);
                if (config == null) {
                    config = new HashMap<String,String>();
                    _prefix_config_map.put(prop_prefix, config);
                }
                config.put(prop_suffix, prop_value);
                if (NAMESPACE.equals(prop_suffix)) {
                    _namespace_prefix_map.put(prop_value, prop_prefix);
                } else if (NO_NAMESPACE_SCHEMA.equals(prop_suffix)) {
                    _nonamespace_location_map.put(prop_value, prop_prefix);
                }
            }
        }
    }

    /**
     * Gets a property value based on property name and namespace of the property.
     * @param property the property name
     * @param namespace the namespace of the property
     * @return the property value, or null if it doesn't exist
     */
    public String getProperty(String property, String namespace) {
        String prop_prefix = _namespace_prefix_map.get(namespace);
        if (prop_prefix != null) {
            Map<String,String> config = _prefix_config_map.get(prop_prefix);
            if (config != null) {
                return config.get(property);
            }
        }

        // try also find given namespace in prefix map
        Map<String,String> config = _prefix_config_map.get(namespace);
        if (config != null) {
            return config.get(property);
        }
        return null;
    }

    /**
     * Creates a Schema based on the combined schema documents/definitions found that are associated with the specified namespaces.
     * @param namespaces the namespaces of the schemas
     * @return the new Schema
     */
    public synchronized Schema getSchema(Set<String> namespaces) {
        Schema schema = _namespaces_schema_map.get(namespaces);
        if (schema == null) {
            Map<String, Source> nsSourceMap = new TreeMap<String, Source>(new NamespaceComparator());
            try {
                for (String namespace : namespaces) {
                    String schemaLocation = getSchemaLocation(namespace);
                    if (schemaLocation != null) {
                        URL url = Classes.getResource(schemaLocation, Descriptor.class);
                        if (url != null) {
                            String xsd = new StringPuller().pull(url);
                            nsSourceMap.put(namespace, new StreamSource(new StringReader(xsd)));
                        }
                    }
                }
                if (nsSourceMap.size() > 0) {
                    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                    factory.setResourceResolver(new DescriptorLSResourceResolver(this));
                    Collection<Source> sortedSources = nsSourceMap.values();
                    Source[] schemaSources = sortedSources.toArray(new Source[sortedSources.size()]);
                    schema = factory.newSchema(schemaSources);
                    _namespaces_schema_map.put(namespaces, schema);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return schema;
    }

    /**
     * Creates a new Schema based on the namespaces of the specified Configuration.
     * @param config the Configuration
     * @return the new Schema
     * @see #getSchema(Set)
     */
    public Schema getSchema(Configuration config) {
        if (config != null) {
            return getSchema(config.getChildrenNamespaces());
        }
        return null;
    }

    /**
     * Creates a new Schema based on the namespaces of the Configuration wrapped by the specified Model.
     * @param model the Model
     * @return the new Schema
     * @see #getSchema(Configuration)
     * @see #getSchema(Set)
     */
    public static Schema getSchema(Model model) {
        if (model != null) {
            Descriptor desc = model.getModelDescriptor();
            if (desc != null) {
                return desc.getSchema(model.getModelConfiguration());
            }
        }
        return null;
    }

    /**
     * Creates a Validator based on the combined schema documents/definitions found that are associated with the specified namespace.
     * @param namespaces the namespaces of the schemas
     * @return the new Validator
     */
    public Validator getValidator(Set<String> namespaces) {
        return getValidator(getSchema(namespaces));
    }

    /**
     * Creates a new Validator based on the namespaces of the specified Configuration.
     * @param config the Configuration
     * @return the new Validator
     * @see #getValidator(Set)
     */
    public Validator getValidator(Configuration config) {
        return getValidator(getSchema(config));
    }

    /**
     * Creates a new Validator based on the namespaces of the Configuration wrapped by the specified Model.
     * @param model the Model
     * @return the new Validator
     * @see #getValidator(Configuration)
     * @see #getValidator(Set)
     */
    public Validator getValidator(Model model) {
        return getValidator(getSchema(model));
    }

    private Validator getValidator(Schema schema) {
        if (schema != null) {
            Validator validator = schema.newValidator();
            validator.setResourceResolver(new DescriptorLSResourceResolver(this));
            return validator;
        }
        return null;
    }

    /**
     * Gets the location property, based on the specified namespace.
     * @param namespace the namespace
     * @return the {@link #LOCATION} property value
     */
    public String getLocation(String namespace) {
        return getProperty(LOCATION, namespace);
    }

    /**
     * Gets the schema location, based on the specified namespace.
     * @param namespace the namespace
     * @return the location property value, which is a combination of {@link Descriptor#getLocation(String)} and the {@link #SCHEMA} property value.
     */
    public String getSchemaLocation(String namespace) {
        return getSchemaLocation(namespace, getProperty(SCHEMA, namespace));
    }

    private String getSchemaLocation(String namespace, String schema) {
        String schemaLocation = null;

        if (namespace != null) {
            if (schema == null) {
                schema = getProperty(SCHEMA, namespace);
            }
            String location = _nonamespace_location_map.containsKey(schema)
                ? getLocation(_nonamespace_location_map.get(schema))
                : getLocation(namespace);
            if (location != null) {
                schemaLocation = location + "/" + schema;
                schemaLocation = schemaLocation.replaceAll("\\\\", "/").replaceAll("//", "/");
            }
        }
        if (schemaLocation == null && schema != null && schema.startsWith("http://")) {
            schema = schema.substring(7);
            int pos = schema.indexOf('/');
            if (pos != -1) {
                String domain = schema.substring(0, pos);
                StringTokenizer st = new StringTokenizer(domain, ".");
                int len = st.countTokens();
                String[] parts = new String[len];
                for (int i=0; i < len; i++) {
                    parts[(len-1)-i] = st.nextToken();
                }
                StringBuilder sb = new StringBuilder();
                sb.append('/');
                for (int i=0; i < len; i++) {
                    sb.append(parts[i]);
                    if (i != len-1) {
                        sb.append('/');
                    }
                }
                domain = sb.toString();
                String path = schema.substring(pos, schema.length());
                schemaLocation = domain + path;
            }
        }
        return schemaLocation;
    }

    /**
     * Lazily gets (and possibly creating and caching) a Marshaller based on the specified namespace.
     * @param namespace the namespace
     * @return the appropriate Marshaller to use
     */
    public synchronized Marshaller getMarshaller(String namespace) {
        return getMarshaller(namespace, Classes.getClassLoader(Descriptor.class));
    }
    
    /**
     * Lazily gets (and possibly creating and caching) a Marshaller based on the specified namespace and ClassLoader.
     * @param namespace the namespace
     * @param loader ClassLoader
     * @return the appropriate Marshaller to use
     */
    public synchronized Marshaller getMarshaller(String namespace, ClassLoader loader) {
        if (namespace == null) {
            return null;
        }
        Marshaller marshaller = _namespace_marshaller_map.get(namespace);
        if (marshaller == null) {
            String typeName = getProperty(MARSHALLER, namespace);
            if (typeName != null) {
                Class<?> type = Classes.forName(typeName, loader);
                if (type == null) {
                    throw ConfigMessages.MESSAGES.cannotFindMarshaller(typeName, loader);
                }
                marshaller = (Marshaller) Construction.construct(type, new Class<?>[]{Descriptor.class}, new Object[]{this});
                if (marshaller != null) {
                    _namespace_marshaller_map.put(namespace, marshaller);
                }
            }
        }
        return marshaller;
    }

    /**
     * Lazily gets (and possibly creating and caching) a Marshaller based on the namespace of the specified Configuration.
     * @param config the Configuration
     * @return the appropriate Marshaller to use
     * @see #getMarshaller(String)
     */
    public Marshaller getMarshaller(Configuration config) {
        if (config != null) {
            QName qname = config.getQName();
            if (qname != null) {
                String namespace = qname.getNamespaceURI();
                if (namespace != null) {
                    return getMarshaller(namespace);
                }
            }
        }
        return null;
    }

    /**
     * Lazily gets (and possibly creating and caching) a Marshaller based on the namespace of the wrapped Configuration of the specified Model.
     * @param model the Model
     * @return the appropriate Marshaller to use
     * @see #getMarshaller(Configuration)
     * @see #getMarshaller(String)
     */
    public static Marshaller getMarshaller(Model model) {
        if (model != null) {
            Descriptor desc = model.getModelDescriptor();
            if (desc != null) {
                return desc.getMarshaller(model.getModelConfiguration());
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return _all_properties_map.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime  * result + ((_all_properties_map == null) ? 0 : _all_properties_map.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Descriptor other = (Descriptor)obj;
        if (_all_properties_map == null) {
            if (other._all_properties_map != null) {
                return false;
            }
        } else if (!_all_properties_map.equals(other._all_properties_map)) {
            return false;
        }
        return true;
    }

    private static final class DescriptorLSResourceResolver implements LSResourceResolver {

        private Descriptor _descriptor;

        private DescriptorLSResourceResolver(Descriptor descriptor) {
            _descriptor = descriptor;
        }

        public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
            String schemaLocation = _descriptor.getSchemaLocation(namespaceURI, systemId);
            if (schemaLocation == null && baseURI != null && baseURI.endsWith(".dtd")) {
                String schema = baseURI.substring(0, baseURI.lastIndexOf('/')+1) + systemId;
                schemaLocation = _descriptor.getSchemaLocation(null, schema);
            }
            if (schemaLocation != null) {
                try {
                    String xsd = new StringPuller().pull(schemaLocation, getClass());
                    if (xsd != null) {
                        return new DescriptorLSInput(xsd, publicId, systemId, baseURI);
                    }
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            }
            return null;
        }

    }

    private static final class DescriptorLSInput implements LSInput {

        private Reader _characterStream;
        private InputStream _byteStream;
        private String _stringData;
        private String _publicId;
        private String _systemId;
        private String _baseURI;
        private String _encoding;
        private boolean _certifiedText;

        private DescriptorLSInput(String xsd, String publicId, String systemId, String baseURI) {
            setCharacterStream(new StringReader(xsd));
            setByteStream(new ByteArrayInputStream(xsd.getBytes()));
            setStringData(xsd);
            setPublicId(publicId);
            setSystemId(systemId);
            setBaseURI(baseURI);
            setEncoding("UTF-8");
            setCertifiedText(false);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Reader getCharacterStream() {
            return _characterStream;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setCharacterStream(Reader characterStream) {
            _characterStream = characterStream;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public InputStream getByteStream() {
            return _byteStream;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setByteStream(InputStream byteStream) {
            _byteStream = byteStream;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getStringData() {
            return _stringData;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setStringData(String stringData) {
            _stringData = stringData;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getSystemId() {
            return _systemId;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getPublicId() {
            return _publicId;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setPublicId(String publicId) {
            _publicId = publicId;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setSystemId(String systemId) {
            _systemId = systemId;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getBaseURI() {
            return _baseURI;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setBaseURI(String baseURI) {
            _baseURI = baseURI;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getEncoding() {
            return _encoding;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setEncoding(String encoding) {
            _encoding = encoding;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean getCertifiedText() {
            return _certifiedText;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setCertifiedText(boolean certifiedText) {
            _certifiedText = certifiedText;
        }

    }

}
