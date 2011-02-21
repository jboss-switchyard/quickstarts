/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.switchyard.config.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
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

import org.switchyard.config.Configuration;
import org.switchyard.config.util.Classes;
import org.switchyard.config.util.PropertiesResource;
import org.switchyard.config.util.StringResource;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * Descriptor.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Descriptor {

    public static final String DEFAULT_PROPERTIES = "/org/switchyard/config/model/descriptor.properties";
    public static final String NAMESPACE = "namespace";
    public static final String SCHEMA = "schema";
    public static final String LOCATION = "location";
    public static final String MARSHALLER = "marshaller";

    private Map<String,String> _all_properties_map = new TreeMap<String,String>();
    private Map<String,Map<String,String>> _prefix_config_map = new HashMap<String,Map<String,String>>();
    private Map<String,String> _namespace_prefix_map = new HashMap<String,String>();

    private Map<Set<String>,Schema> _namespaces_schema_map = new HashMap<Set<String>,Schema>();;
    private Map<String,Marshaller> _namespace_marshaller_map = new HashMap<String,Marshaller>();

    public Descriptor() {
        String dp = DEFAULT_PROPERTIES.substring(1);
        Properties props = new Properties();
        PropertiesResource props_res = new PropertiesResource();
        try {
            List<URL> urls = Classes.getResources(dp, Descriptor.class);
            for (URL url : urls) {
                Properties url_props = props_res.pull(url);
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
        setProperties(props);
    }

    public Descriptor(Properties props) {
        setProperties(props);
    }

    private void setProperties(Properties props) {
        Enumeration<?> e = props.propertyNames();
        while (e.hasMoreElements()) {
            String prop_name = (String)e.nextElement();
            String prop_value = props.getProperty(prop_name);
            if (prop_value != null) {
                _all_properties_map.put(prop_name, prop_value);
                StringTokenizer tokenizer = new StringTokenizer(prop_name, ".");
                String prop_prefix = tokenizer.nextToken().trim();
                String prop_suffix = tokenizer.nextToken().trim();
                Map<String,String> config = _prefix_config_map.get(prop_prefix);
                if (config == null) {
                    config = new HashMap<String,String>();
                    _prefix_config_map.put(prop_prefix, config);
                }
                config.put(prop_suffix, prop_value);
                if (NAMESPACE.equals(prop_suffix)) {
                    _namespace_prefix_map.put(prop_value, prop_prefix);
                }
            }
        }
    }

    public String getProperty(String property, String namespace) {
        String prop_prefix = _namespace_prefix_map.get(namespace);
        if (prop_prefix != null) {
            Map<String,String> config = _prefix_config_map.get(prop_prefix);
            if (config != null) {
                return config.get(property);
            }
        }
        return null;
    }

    public synchronized Schema getSchema(Set<String> namespaces) {
        Schema schema = _namespaces_schema_map.get(namespaces);
        if (schema == null) {
            List<Source> sourceList = new ArrayList<Source>();
            try {
                for (String namespace : namespaces) {
                    String schemaLocation = getSchemaLocation(namespace);
                    if (schemaLocation != null) {
                        URL resource = Classes.getResource(schemaLocation);
                        if (resource != null) {
                            String xsd = new StringResource().pull(resource);
                            sourceList.add(new StreamSource(new StringReader(xsd)));
                        }
                    }
                }
                if (sourceList.size() > 0) {
                    Source[] sources = sourceList.toArray(new Source[sourceList.size()]);
                    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                    factory.setResourceResolver(new DescriptorLSResourceResolver(this));
                    schema = factory.newSchema(sources);
                    _namespaces_schema_map.put(namespaces, schema);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return schema;
    }

    public Schema getSchema(Configuration config) {
        if (config != null) {
            return getSchema(config.getNamespaces());
        }
        return null;
    }

    public static Schema getSchema(Model model) {
        if (model != null) {
            Descriptor desc = model.getModelDescriptor();
            if (desc != null) {
                return desc.getSchema(model.getModelConfiguration());
            }
        }
        return null;
    }

    public String getLocation(String namespace) {
        return getProperty(LOCATION, namespace);
    }

    public String getSchemaLocation(String namespace) {
        return getSchemaLocation(namespace, getProperty(SCHEMA, namespace));
    }

    private String getSchemaLocation(String namespace, String schema) {
        if (namespace != null && schema != null) {
            String location = getLocation(namespace);
            String schemaLocation = location + "/" + schema;
            return schemaLocation.replaceAll("//", "/");
        }
        return null;
    }

    public synchronized Marshaller getMarshaller(String namespace) {
        Marshaller marshaller = _namespace_marshaller_map.get(namespace);
        if (marshaller == null) {
            String name = getProperty(MARSHALLER, namespace);
            if (name != null) {
                try {
                    Class<?> clazz = Classes.forName(name, Descriptor.class);
                    Constructor<?> cnstr = clazz.getConstructor(Descriptor.class);
                    marshaller = (Marshaller)cnstr.newInstance(this);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return marshaller;
    }

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

    public static Marshaller getMarshaller(Model model) {
        if (model != null) {
            Descriptor desc = model.getModelDescriptor();
            if (desc != null) {
                return desc.getMarshaller(model.getModelConfiguration());
            }
        }
        return null;
    }

    public String toString() {
        return _all_properties_map.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime  * result + ((_all_properties_map == null) ? 0 : _all_properties_map.hashCode());
        return result;
    }

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
            if (schemaLocation != null) {
                try {
                    String xsd = new StringResource().pull(schemaLocation);
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

        @Override
        public Reader getCharacterStream() {
            return _characterStream;
        }

        @Override
        public void setCharacterStream(Reader characterStream) {
            _characterStream = characterStream;
        }

        @Override
        public InputStream getByteStream() {
            return _byteStream;
        }

        @Override
        public void setByteStream(InputStream byteStream) {
            _byteStream = byteStream;
        }

        @Override
        public String getStringData() {
            return _stringData;
        }

        @Override
        public void setStringData(String stringData) {
            _stringData = stringData;
        }

        @Override
        public String getSystemId() {
            return _systemId;
        }

        @Override
        public String getPublicId() {
            return _publicId;
        }

        @Override
        public void setPublicId(String publicId) {
            _publicId = publicId;
        }

        @Override
        public void setSystemId(String systemId) {
            _systemId = systemId;
        }

        @Override
        public String getBaseURI() {
            return _baseURI;
        }

        @Override
        public void setBaseURI(String baseURI) {
            _baseURI = baseURI;
        }

        @Override
        public String getEncoding() {
            return _encoding;
        }

        @Override
        public void setEncoding(String encoding) {
            _encoding = encoding;
        }

        @Override
        public boolean getCertifiedText() {
            return _certifiedText;
        }

        @Override
        public void setCertifiedText(boolean certifiedText) {
            _certifiedText = certifiedText;
        }
    }

}
