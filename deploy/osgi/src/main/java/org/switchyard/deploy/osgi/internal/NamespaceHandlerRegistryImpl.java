/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.deploy.osgi.internal;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.deploy.osgi.NamespaceHandler;
import org.switchyard.deploy.osgi.NamespaceHandlerRegistry;
import org.switchyard.deploy.osgi.NamespaceHandlerSet;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

/**
 * Default implementation of the NamespaceHandlerRegistry.
 *
 * This registry will track NamespaceHandler objects in the OSGi registry and make
 * them available, calling listeners when handlers are registered or unregistered.
 *
 */
public class NamespaceHandlerRegistryImpl implements NamespaceHandlerRegistry, ServiceTrackerCustomizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NamespaceHandlerRegistryImpl.class);

    // The bundle context is thread safe
    private final BundleContext _bundleContext;

    // The service tracker is thread safe
    private final ServiceTracker _tracker;

    // The handlers map is concurrent
    private final ConcurrentHashMap<URI, CopyOnWriteArraySet<NamespaceHandler>> _handlers =
            new ConcurrentHashMap<URI, CopyOnWriteArraySet<NamespaceHandler>>();

    // Access to the LRU schemas map is synchronized on itself
    private final LRUMap<Map<URI, NamespaceHandler>, Reference<Schema>> _schemas =
            new LRUMap<Map<URI, NamespaceHandler>, Reference<Schema>>(10);

    // Access to this factory is synchronized on itself
    private final SchemaFactory _schemaFactory =
            SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

    // Access to this variable is not synchronized.  The list itself is concurrent
    private final CopyOnWriteArrayList<NamespaceHandlerSetImpl> _sets =
            new CopyOnWriteArrayList<NamespaceHandlerSetImpl>();

    /**
     * Create new instance of NamespaceHandlerRegistryImpl.
     * @param bundleContext bundleContext
     */
    public NamespaceHandlerRegistryImpl(BundleContext bundleContext) {
        _bundleContext = bundleContext;
        _tracker = new ServiceTracker(bundleContext, NamespaceHandler.class.getName(), this);
        _tracker.open();
    }

    @Override
    public Object addingService(ServiceReference reference) {
        LOGGER.debug("Adding NamespaceHandler " + reference.toString());
        NamespaceHandler handler = (NamespaceHandler) _bundleContext.getService(reference);
        if (handler != null) {
            try {
                Map<String, Object> props = new HashMap<String, Object>();
                for (String name : reference.getPropertyKeys()) {
                    props.put(name, reference.getProperty(name));
                }
                registerHandler(handler, props);
            } catch (Exception e) {
                LOGGER.warn("Error registering NamespaceHandler", e);
            }
        } else {
            LOGGER.warn("Error resolving NamespaceHandler, null Service obtained from tracked ServiceReference {} for bundle {}, ver {}",
                    new Object[] { reference.toString(), reference.getBundle().getSymbolicName(), reference.getBundle().getVersion() });
        }
        return handler;
    }

    @Override
    public void modifiedService(ServiceReference reference, Object service) {
        removedService(reference, service);
        addingService(reference);
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        try {
            LOGGER.debug("Removing NamespaceHandler " + reference.toString());
            NamespaceHandler handler = (NamespaceHandler) service;
            Map<String, Object> props = new HashMap<String, Object>();
            for (String name : reference.getPropertyKeys()) {
                props.put(name, reference.getProperty(name));
            }
            unregisterHandler(handler, props);
        } catch (Exception e) {
            LOGGER.warn("Error unregistering NamespaceHandler", e);
        }
    }

    public void registerHandler(NamespaceHandler handler, Map properties) {
        List<URI> namespaces = getNamespaces(properties);
        for (URI uri : namespaces) {
            CopyOnWriteArraySet<NamespaceHandler> h = _handlers.putIfAbsent(uri, new CopyOnWriteArraySet<NamespaceHandler>());
            if (h == null) {
                h = _handlers.get(uri);
            }
            if (h.add(handler)) {
                for (NamespaceHandlerSetImpl s : _sets) {
                    s.registerHandler(uri, handler);
                }
            }
        }
    }

    public void unregisterHandler(NamespaceHandler handler, Map properties) {
        List<URI> namespaces = getNamespaces(properties);
        for (URI uri : namespaces) {
            CopyOnWriteArraySet<NamespaceHandler> h = _handlers.get(uri);
            if (!h.remove(handler)) {
                continue;
            }
            for (NamespaceHandlerSetImpl s : _sets) {
                s.unregisterHandler(uri, handler);
            }
        }
        removeSchemasFor(handler);
    }

    private static List<URI> getNamespaces(Map properties) {
        Object ns = properties != null ? properties.get(NamespaceHandler.NAMESPACES) : null;
        if (ns == null) {
            throw new IllegalArgumentException("NamespaceHandler service does not have an associated "
                    + NamespaceHandler.NAMESPACES + " property defined");
        } else if (ns instanceof URI[]) {
            return Arrays.asList((URI[]) ns);
        } else if (ns instanceof URI) {
            return Collections.singletonList((URI) ns);
        } else if (ns instanceof String) {
            return Collections.singletonList(URI.create((String) ns));
        } else if (ns instanceof String[]) {
            String[] strings = (String[]) ns;
            List<URI> namespaces = new ArrayList<URI>(strings.length);
            for (String string : strings) {
                namespaces.add(URI.create(string));
            }
            return namespaces;
        } else if (ns instanceof Collection) {
            Collection col = (Collection) ns;
            List<URI> namespaces = new ArrayList<URI>(col.size());
            for (Object o : col) {
                namespaces.add(toURI(o));
            }
            return namespaces;
        } else if (ns instanceof Object[]) {
            Object[] array = (Object[]) ns;
            List<URI> namespaces = new ArrayList<URI>(array.length);
            for (Object o : array) {
                namespaces.add(toURI(o));
            }
            return namespaces;
        } else {
            throw new IllegalArgumentException("NamespaceHandler service has an associated "
                    + NamespaceHandler.NAMESPACES + " property defined which can not be converted to an array of URI");
        }
    }

    private static URI toURI(Object o) {
        if (o instanceof URI) {
            return (URI) o;
        } else if (o instanceof String) {
            return URI.create((String) o);
        } else {
            throw new IllegalArgumentException("NamespaceHandler service has an associated "
                    + NamespaceHandler.NAMESPACES + " property defined which can not be converted to an array of URI");
        }
    }

    public NamespaceHandlerSet getNamespaceHandlers(Set<URI> uris, Bundle bundle) {
        NamespaceHandlerSetImpl s = new NamespaceHandlerSetImpl(uris, bundle);
        _sets.add(s);
        return s;
    }

    public void destroy() {
        _tracker.close();
    }

    private Schema getSchema(Map<URI, NamespaceHandler> handlers,
                             final Bundle bundle,
                             final Properties schemaMap) throws IOException, SAXException {
        if (schemaMap != null && !schemaMap.isEmpty()) {
            return createSchema(handlers, bundle, schemaMap);
        }
        // Find a schema that can handle all the requested namespaces
        // If it contains additional namespaces, it should not be a problem since
        // they won't be used at all
        Schema schema = getExistingSchema(handlers);
        if (schema == null) {
            // Create schema
            schema = createSchema(handlers, bundle, schemaMap);
            cacheSchema(handlers, schema);
        }
        return schema;
    }

    private Schema getExistingSchema(Map<URI, NamespaceHandler> handlers) {
        synchronized (_schemas) {
            for (Map<URI, NamespaceHandler> key : _schemas.keySet()) {
                boolean found = true;
                for (URI uri : handlers.keySet()) {
                    if (!handlers.get(uri).equals(key.get(uri))) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    return _schemas.get(key).get();
                }
            }
            return null;
        }
    }

    private void removeSchemasFor(NamespaceHandler handler) {
        synchronized (_schemas) {
            List<Map<URI, NamespaceHandler>> keys = new ArrayList<Map<URI, NamespaceHandler>>();
            for (Map<URI, NamespaceHandler> key : _schemas.keySet()) {
                if (key.values().contains(handler)) {
                    keys.add(key);
                }
            }
            for (Map<URI, NamespaceHandler> key : keys) {
                _schemas.remove(key);
            }
        }
    }

    private void cacheSchema(Map<URI, NamespaceHandler> handlers, Schema schema) {
        synchronized (_schemas) {
            // Remove schemas that are fully included
            for (Iterator<Map<URI, NamespaceHandler>> iterator = _schemas.keySet().iterator(); iterator.hasNext();) {
                Map<URI, NamespaceHandler> key = iterator.next();
                boolean found = true;
                for (URI uri : key.keySet()) {
                    if (!key.get(uri).equals(handlers.get(uri))) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    iterator.remove();
                    break;
                }
            }
            // Add our new schema
            _schemas.put(handlers, new SoftReference<Schema>(schema));
        }
    }

    private Schema createSchema(Map<URI, NamespaceHandler> handlers,
                                Bundle bundle,
                                Properties schemaMap) throws IOException, SAXException {
        final List<StreamSource> schemaSources = new ArrayList<StreamSource>();
        try {
            // Create a schema for all namespaces known at this point
            // It will speed things as it can be reused for all other deployments
            for (URI ns : handlers.keySet()) {
                URL url = handlers.get(ns).getSchemaLocation(ns.toString());
                if (url == null) {
                    LOGGER.warn("No URL is defined for schema " + ns + ". This schema will not be validated");
                } else {
                    schemaSources.add(new StreamSource(url.openStream(), url.toExternalForm()));
                }
            }
            for (Object ns : schemaMap.values()) {
                URL url = bundle.getResource(ns.toString());
                if (url == null) {
                    LOGGER.warn("No URL is defined for schema " + ns + ". This schema will not be validated");
                } else {
                    schemaSources.add(new StreamSource(url.openStream(), url.toExternalForm()));
                }
            }
            synchronized (_schemaFactory) {
                _schemaFactory.setResourceResolver(new BundleResourceResolver(schemaMap, bundle, schemaSources));
                return _schemaFactory.newSchema(schemaSources.toArray(new Source[schemaSources.size()]));
            }
        } finally {
            for (StreamSource s : schemaSources) {
                closeQuietly(s.getInputStream());
            }
        }
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            // Ignore
        }
    }

    private class BundleResourceResolver implements LSResourceResolver {
        private final Properties _schemaMap;
        private final Bundle _bundle;
        private final List<StreamSource> _schemaSources;

        public BundleResourceResolver(Properties schemaMap, Bundle bundle, List<StreamSource> schemaSources) {
            _schemaMap = schemaMap;
            _bundle = bundle;
            _schemaSources = schemaSources;
        }

        public LSInput resolveResource(String type,
                                       final String namespaceURI,
                                       final String publicId,
                                       String systemId, String baseURI) {
            String loc = null;
            if (namespaceURI != null) {
                loc = _schemaMap.getProperty(namespaceURI);
            }
            if (loc == null && publicId != null) {
                loc = _schemaMap.getProperty(publicId);
            }
            if (loc == null && systemId != null) {
                loc = _schemaMap.getProperty(systemId);
            }
            if (loc != null) {
                URL url = _bundle.getResource(loc);
                if (url != null) {
                    try {
                        StreamSource source
                                = new StreamSource(url.openStream(), url.toExternalForm());
                        _schemaSources.add(source);
                        return new SourceLSInput(source, publicId, url);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            URI uri = URI.create(namespaceURI);
            Set<NamespaceHandler> hs = NamespaceHandlerRegistryImpl.this._handlers.get(uri);
            if (hs == null) {
                return null;
            }
            for (NamespaceHandler h : hs) {
                URL url = h.getSchemaLocation(namespaceURI);
                if (url != null) {
                    // handling include-relative-path case
                    if (systemId != null && !systemId.matches("^[a-z][-+.0-9a-z]*:.*")) {
                        try {
                            url = new URL(url, systemId);
                        } catch (Exception e) {
                            // ignore and use the given systemId
                        }
                    }
                    try {
                        final StreamSource source = new StreamSource(url.openStream(), url.toExternalForm());
                        _schemaSources.add(source);
                        return new SourceLSInput(source, publicId, url);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return null;
        }
    }

    private class SourceLSInput implements LSInput {
        private final StreamSource _source;
        private final URL _systemId;
        private final String _publicId;

        public SourceLSInput(StreamSource source, String publicId, URL systemId) {
            _source = source;
            _publicId = publicId;
            _systemId = systemId;
        }

        public Reader getCharacterStream() {
            return null;
        }

        public void setCharacterStream(Reader characterStream) {
        }

        public InputStream getByteStream() {
            return _source.getInputStream();
        }

        public void setByteStream(InputStream byteStream) {
        }

        public String getStringData() {
            return null;
        }

        public void setStringData(String stringData) {
        }

        public String getSystemId() {
            return _systemId.toExternalForm();
        }

        public void setSystemId(String systemId) {
        }

        public String getPublicId() {
            return _publicId;
        }

        public void setPublicId(String publicId) {
        }

        public String getBaseURI() {
            return null;
        }

        public void setBaseURI(String baseURI) {
        }

        public String getEncoding() {
            return null;
        }

        public void setEncoding(String encoding) {
        }

        public boolean getCertifiedText() {
            return false;
        }

        public void setCertifiedText(boolean certifiedText) {
        }
    }

    protected class NamespaceHandlerSetImpl implements NamespaceHandlerSet {

        private final List<Listener> _listeners;
        private final Bundle _bundle;
        private final Set<URI> _namespaces;
        private final Map<URI, NamespaceHandler> _nsHandlers;
        private final Properties _schemaMap = new Properties();
        private Schema _schema;

        public NamespaceHandlerSetImpl(Set<URI> namespaces, Bundle bundle) {
            _listeners = new CopyOnWriteArrayList<Listener>();
            _namespaces = namespaces;
            _bundle = bundle;
            _nsHandlers = new HashMap<URI, NamespaceHandler>();
            for (URI ns : namespaces) {
                findCompatibleNamespaceHandler(ns);
            }
            URL url = bundle.getResource("OSGI-INF/switchyard/schema.map");
            if (url != null) {
                InputStream ins = null;
                try {
                    ins = url.openStream();
                    _schemaMap.load(ins);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    //ignore
                } finally {
                    closeQuietly(ins);
                }
            }
            for (Object ns : _schemaMap.keySet()) {
                try {
                    this._namespaces.remove(new URI(ns.toString()));
                } catch (URISyntaxException e) {
                    //ignore
                }
            }
        }

        public boolean isComplete() {
            return _nsHandlers.size() == _namespaces.size();
        }

        public Set<URI> getNamespaces() {
            return _namespaces;
        }

        public NamespaceHandler getNamespaceHandler(URI namespace) {
            return _nsHandlers.get(namespace);
        }

        public Schema getSchema() throws SAXException, IOException {
            if (!isComplete()) {
                throw new IllegalStateException("NamespaceHandlerSet is not complete");
            }
            if (_schema == null) {
                _schema = NamespaceHandlerRegistryImpl.this.getSchema(_nsHandlers, _bundle, _schemaMap);
            }
            return _schema;
        }

        public void addListener(Listener listener) {
            _listeners.add(listener);
        }

        public void removeListener(Listener listener) {
            _listeners.remove(listener);
        }

        public void destroy() {
            NamespaceHandlerRegistryImpl.this._sets.remove(this);
        }

        public void registerHandler(URI uri, NamespaceHandler handler) {
            if (_namespaces.contains(uri) && _nsHandlers.get(uri) == null) {
                if (findCompatibleNamespaceHandler(uri) !=  null) {
                    for (Listener listener : _listeners) {
                        try {
                            listener.namespaceHandlerRegistered(uri);
                        } catch (Throwable t) {
                            LOGGER.debug("Unexpected exception when notifying a NamespaceHandler listener", t);
                        }
                    }
                }
            }
        }

        public void unregisterHandler(URI uri, NamespaceHandler handler) {
            if (_nsHandlers.get(uri) == handler) {
                _nsHandlers.remove(uri);
                for (Listener listener : _listeners) {
                    try {
                        listener.namespaceHandlerUnregistered(uri);
                    } catch (Throwable t) {
                        LOGGER.debug("Unexpected exception when notifying a NamespaceHandler listener", t);
                    }
                }
            }
        }

        private NamespaceHandler findCompatibleNamespaceHandler(URI ns) {
            Set<NamespaceHandler> candidates = NamespaceHandlerRegistryImpl.this._handlers.get(ns);
            if (candidates != null) {
                for (NamespaceHandler h : candidates) {
                    Set<Class> classes = h.getManagedClasses();
                    boolean compat = true;
                    if (classes != null) {
                        Set<Class> allClasses = new HashSet<Class>();
                        for (Class cl : classes) {
                            for (Class c = cl; c != null; c = c.getSuperclass()) {
                                allClasses.add(c);
                                for (Class i : c.getInterfaces()) {
                                    allClasses.add(i);
                                }
                            }
                        }
                        for (Class cl : allClasses) {
                            Class clb;
                            try {
                                clb = _bundle.loadClass(cl.getName());
                                if (clb != cl) {
                                    compat = false;
                                    break;
                                }
                            } catch (ClassNotFoundException e) {
                                // Ignore
                            } catch (NoClassDefFoundError e) {
                                // Ignore
                            }
                        }
                    }
                    if (compat) {
                        _nsHandlers.put(ns, h);
                        return h;
                    }
                }
            }
            return null;
        }
    }

    public static class LRUMap<K,V> extends AbstractMap<K,V> {

        private final int bound;
        private final LinkedList<Entry<K,V>> entries = new LinkedList<Entry<K,V>>();

        private static class LRUEntry<K,V> implements Entry<K,V> {
            private final K key;
            private final V value;

            private LRUEntry(K key, V value) {
                this.key = key;
                this.value = value;
            }

            public K getKey() {
                return key;
            }

            public V getValue() {
                return value;
            }

            public V setValue(V value) {
                throw new UnsupportedOperationException();
            }
        }

        private LRUMap(int bound) {
            this.bound = bound;
        }

        public V get(Object key) {
            if (key == null) {
                throw new NullPointerException();
            }
            for (Entry<K,V> e : entries) {
                if (e.getKey().equals(key)) {
                    entries.remove(e);
                    entries.addFirst(e);
                    return e.getValue();
                }
            }
            return null;
        }

        public V put(K key, V value) {
            if (key == null) {
                throw new NullPointerException();
            }
            V old = null;
            for (Entry<K,V> e : entries) {
                if (e.getKey().equals(key)) {
                    entries.remove(e);
                    old = e.getValue();
                    break;
                }
            }
            if (value != null) {
                entries.addFirst(new LRUEntry<K,V>(key, value));
                while (entries.size() > bound) {
                    entries.removeLast();
                }
            }
            return old;
        }

        public Set<Entry<K, V>> entrySet() {
            return new AbstractSet<Entry<K,V>>() {
                public Iterator<Entry<K, V>> iterator() {
                    return entries.iterator();
                }

                public int size() {
                    return entries.size();
                }
            };
        }
    }

}
