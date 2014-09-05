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

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.ServiceDomain;
import org.switchyard.common.io.pull.ElementPuller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Marshaller;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.Component;
import org.switchyard.deploy.internal.Deployment;
import org.switchyard.deploy.osgi.ComponentRegistry;
import org.switchyard.deploy.osgi.NamespaceHandler;
import org.switchyard.deploy.osgi.NamespaceHandlerSet;
import org.switchyard.deploy.osgi.SwitchYardContainer;
import org.switchyard.deploy.osgi.SwitchYardEvent;
import org.switchyard.deploy.osgi.base.SimpleExtension;
import org.switchyard.transform.internal.DuplicateTransformerException;
import org.switchyard.transform.internal.TransformerRegistryLoader;
import org.switchyard.transform.osgi.internal.TransformSource;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * SwitchYardContainerImpl.
 */
public class SwitchYardContainerImpl extends SimpleExtension
        implements NamespaceHandlerSet.Listener, ComponentRegistry.Listener, Runnable, SwitchYardContainer {

    public static final String SWITCHYARD_DEPLOYMENT_BUNDLE = "switchyard.deployment.bundle";
    public static final String CONTAINER_SYMBOLIC_NAME_PROPERTY = "switchyard.container.symbolicname";
    public static final String CONTAINER_VERSION_PROPERTY = "switchyard.container.version";

    private static final Logger logger = LoggerFactory.getLogger(SwitchYardExtender.class);

    public enum State {
        Unknown,
        WaitForCdi,
        WaitForNamespaceHandlers,
        WaitForComponents,
        Created,
        Failed,
    }


    private final SwitchYardExtender _extender;
    private final Bundle _extenderBundle;
    private State _state = State.Unknown;
    private Element _xml;
    private SwitchYardModel _model;
    private Set<URI> _namespaces;
    private NamespaceHandlerSet _nhs;
    private Set<String> _types;
    private ServiceDomain _domain;
    private Deployment _deployment;
    private final AtomicBoolean _scheduled = new AtomicBoolean();
    private final AtomicBoolean _destroyed = new AtomicBoolean(false);
    private final ExecutorService _executors;
    private ServiceRegistration<SwitchYardContainer> _registration;
    private ServiceTracker _cdiContainerTracker;
    private Object _cdiContainer;

    public SwitchYardContainerImpl(SwitchYardExtender extender, Bundle bundle, ExecutorService executor) {
        super(bundle);
        _extender = extender;
        _extenderBundle = extender.getBundleContext().getBundle();
        _executors = executor != null ? new ExecutorServiceWrapper(executor) : null;
    }

    @Override
    protected Object getLock() {
        return _scheduled;
    }

    public void schedule() {
        if (_scheduled.compareAndSet(false, true) && !_destroyed.get()) {
            _executors.submit(this);
        }
    }

    public void run() {
        _scheduled.set(false);
        synchronized (_scheduled) {
            doStart();
        }
    }

    @Override
    protected void doStart() {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(_bundle.adapt(BundleWiring.class).getClassLoader());
            for (;;) {
                if (_destroyed.get()) {
                    return;
                }
                if (_bundle.getState() != Bundle.ACTIVE && _bundle.getState() != Bundle.STARTING) {
                    return;
                }
                if (_bundle.getBundleContext() != _bundleContext) {
                    return;
                }
                logger.debug("Running switchyard container for bundle {} in state {}", _bundle.getSymbolicName(), _state);
                switch (_state) {
                    case Unknown: {
                        dispatch(SwitchYardEvent.CREATING);
                        boolean needsCdi = false;
                        List<BundleWire> wires = _bundle.adapt(BundleWiring.class).getRequiredWires("osgi.extender");
                        for (BundleWire wire : wires) {
                            String filterStr = wire.getRequirement().getDirectives().get("filter");
                            Filter filter = FrameworkUtil.createFilter(filterStr);
                            Dictionary<String, Object> props = new Hashtable<String, Object>();
                            props.put("osgi.extender", "pax.cdi");
                            needsCdi = filter.match(props);
                        }
                        if (needsCdi) {
                            String filter = "(&(objectClass=org.ops4j.pax.cdi.spi.CdiContainer)(bundleId=" + _bundle.getBundleId() + "))";
                            _cdiContainerTracker = new ServiceTracker(_bundleContext, FrameworkUtil.createFilter(filter), new ServiceTrackerCustomizer() {
                                @Override
                                public Object addingService(ServiceReference reference) {
                                    synchronized (_scheduled) {
                                        _cdiContainer = _bundleContext.getService(reference);
                                    }
                                    schedule();
                                    return _cdiContainer;
                                }
                                @Override
                                public void modifiedService(ServiceReference reference, Object service) {
                                }
                                @Override
                                public void removedService(ServiceReference reference, Object service) {
                                    _bundleContext.ungetService(reference);
                                    enterGracePeriod();
                                }
                            });
                            _cdiContainerTracker.open();
                        }
                        _state = State.WaitForCdi;
                        break;
                    }
                    case WaitForCdi: {
                        if (_cdiContainerTracker != null) {
                            if (_cdiContainer == null) {
                                String filter = "(&(objectClass=org.ops4j.pax.cdi.spi.CdiContainer)(bundleId=" + _bundle.getBundleId() + "))";
                                dispatch(SwitchYardEvent.GRACE_PERIOD, Collections.singleton(filter));
                                return;
                            }
                        }
                        if (_nhs == null) {
                            URL configUrl = getBundle().getEntry(SwitchYardExtender.SWITCHYARD_XML);
                            if (configUrl == null) {
                                configUrl = getBundle().getEntry(SwitchYardExtender.WEBINF_SWITCHYARD_XML);
                            }
                            InputStream configStream = configUrl.openStream();
                            try {
                                _xml = new ElementPuller().pull(configStream);
                            } finally {
                                configStream.close();
                            }
                            _namespaces = findNamespaces(new HashSet<URI>(), _xml);
                            _nhs = _extender.getNamespaceHandlerRegistry().getNamespaceHandlers(_namespaces, getBundle());
                            _nhs.addListener(this);
                        }
                        _state = State.WaitForNamespaceHandlers;
                        break;
                    }
                    case WaitForNamespaceHandlers: {
                        List<String> missing = new ArrayList<String>();
                        List<URI> missingURIs = new ArrayList<URI>();
                        for (URI ns : _namespaces) {
                            if (_nhs.getNamespaceHandler(ns) == null) {
                                missing.add("(&(" + Constants.OBJECTCLASS + "=" + NamespaceHandler.class.getName() + ")(" + NamespaceHandler.NAMESPACES + "=" + ns + "))");
                                missingURIs.add(ns);
                                dispatch(SwitchYardEvent.GRACE_PERIOD, missing);
                            }
                        }
                        if (missing.size() > 0) {
                            logger.info("Bundle {} is waiting for namespace handlers {}", getBundle().getSymbolicName(), missingURIs);
                            return;
                        }
                        _model = new ModelPuller<SwitchYardModel>(new OsgiDescriptor(_nhs)).pull(_xml);
                        _types = new HashSet<String>();
                        if (_model.getComposite() != null) {
                            for (CompositeReferenceModel reference : _model.getComposite().getReferences()) {
                                for (BindingModel binding : reference.getBindings()) {
                                    _types.add(binding.getType());
                                }
                            }
                            for (ComponentModel component : _model.getComposite().getComponents()) {
                                ComponentImplementationModel impl = component.getImplementation();
                                if (impl == null) {
                                    throw new IllegalStateException("Component implementation should not be null");
                                }
                                _types.add(impl.getType());
                            }
                            for (CompositeServiceModel service : _model.getComposite().getServices()) {
                                for (BindingModel binding : service.getBindings()) {
                                    _types.add(binding.getType());
                                }
                            }
                        } else {
                            logger.info("A composite element is missing from the switchyard.xml");
                        }
                        _extender.getComponentRegistry().addListener(this);
                        _state = State.WaitForComponents;
                        break;
                    }
                    case WaitForComponents: {
                        List<Component> components = new ArrayList<Component>();
                        List<String> missingTypes = new ArrayList<String>();
                        for (String type : _types) {
                            Component component = _extender.getComponentRegistry().getComponent(type);
                            if (component == null) {
                                missingTypes.add(type);
                            } else {
                                components.add(component);
                            }
                        }
                        if (!missingTypes.isEmpty()) {
                            logger.info("Bundle {} is waiting for components {}", getBundle().getSymbolicName(), missingTypes);
                            dispatch(SwitchYardEvent.GRACE_PERIOD, missingTypes);
                            return;
                        }

                        ClassLoader oldTccl = Thread.currentThread().getContextClassLoader();
                        ClassLoader newTccl = oldTccl;
                        if (_cdiContainer != null) {
                            try {
                                Method method = _cdiContainer.getClass().getMethod("getContextClassLoader");
                                newTccl = (ClassLoader) method.invoke(_cdiContainer);
                            } catch (Throwable t) {
                                // Ignore
                            }
                        }
                        try {
                            Thread.currentThread().setContextClassLoader(newTccl);
                            _domain = _extender.getDomainManager().createDomain(getBundleContext(), _model.getQName(), _model);
                            _domain.setProperty(SWITCHYARD_DEPLOYMENT_BUNDLE, getBundle());
                            
                            registerOOTBTransformers();
                            
                            List<Activator> activators = new ArrayList<Activator>();
                            for (Component component : components) {
                                activators.add(component.createActivator(_domain));
                            }
                            _deployment = new Deployment(_model);
                            _deployment.init(_domain, activators);
                            _deployment.start();
                        } finally {
                            Thread.currentThread().setContextClassLoader(oldTccl);
                        }
                        // Register the BlueprintContainer in the OSGi registry
                        int bs = _bundle.getState();
                        if (_registration == null && (bs == Bundle.ACTIVE || bs == Bundle.STARTING)) {
                            Dictionary<String, Object> props = new Hashtable<String, Object>();
                            props.put(CONTAINER_SYMBOLIC_NAME_PROPERTY, _bundle.getSymbolicName());
                            props.put(CONTAINER_VERSION_PROPERTY, _bundle.getVersion());
                            _registration = _bundleContext.registerService(SwitchYardContainer.class, this, props);
                        }
                        dispatch(SwitchYardEvent.CREATED);
                        _state = State.Created;
                        break;
                    }
                    case Created:
                    case Failed:
                        return;
                }
            }
        } catch (Throwable t) {
            try {
                _state = State.Failed;
                logger.error("Unable to start switchyard for bundle " + getBundle().getSymbolicName(), t);
                dispatch(SwitchYardEvent.FAILURE, t);
                destroyDeployment();
            } catch (RuntimeException re) {
                logger.debug("Tidying up components failed. ", re);
                throw re;
            }
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    @Override
    protected void doDestroy() throws Exception {
        dispatch(SwitchYardEvent.DESTROYING);
        _executors.shutdownNow();
        try {
            if (_registration != null) {
                _registration.unregister();
                _registration = null;
            }
        } catch (Throwable t) {
            logger.debug("Error unregistering Switchyard container", t);
        }
        if (_cdiContainerTracker != null) {
            _cdiContainerTracker.close();
            _cdiContainerTracker = null;
        }
        if (_nhs != null) {
            _nhs.removeListener(this);
            _nhs.destroy();
            _nhs = null;
        }
        try {
            _executors.awaitTermination(5 * 60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.debug("Interrupted waiting for executor to shut down");
        }
        destroyDeployment();
        dispatch(SwitchYardEvent.DESTROYED);
        logger.debug("Switchyard container destroyed: {}", _bundleContext);
    }

    private void dispatch(int type) {
        dispatch(new SwitchYardEvent(type, getBundle(), _extenderBundle));
    }

    private void dispatch(int type, Collection<String> deps) {
        String[] depsArray = deps.toArray(new String[deps.size()]);
        dispatch(new SwitchYardEvent(type, getBundle(), _extenderBundle, depsArray));
    }

    private void dispatch(int type, Throwable t) {
        dispatch(new SwitchYardEvent(type, getBundle(), _extenderBundle, t));
    }

    private void dispatch(SwitchYardEvent event) {
        _extender.getEventDispatcher().switchyardEvent(event);
    }

    @Override
    public void namespaceHandlerRegistered(URI uri) {
        if (_nhs.isComplete()) {
            schedule();
        }
    }

    @Override
    public void namespaceHandlerUnregistered(URI uri) {
        if (!_nhs.isComplete()) {
            enterGracePeriod();
        }
    }

    @Override
    public void componentRegistered(String type) {
        if (_types != null && _types.contains(type)) {
            schedule();
        }
    }

    @Override
    public void componentUnregistered(String type) {
        if (_types != null && _types.contains(type)) {
            enterGracePeriod();
        }
    }

    private void destroyDeployment() {
        _extender.getComponentRegistry().removeListener(this);
        if (_deployment != null) {
            _deployment.stop();
            _deployment.destroy();
            _deployment = null;
        }
        if (_domain != null) {
            _domain.destroy();
            _domain = null;
        }
    }

    private void enterGracePeriod() {
        synchronized (_scheduled) {
            if (_destroyed.get()) {
                return;
            }
            try {
                destroyDeployment();
            } catch (Exception e) {
                logger.error("Error while stopping switchyard", e);
            }
            _cdiContainer = null;
            _state = State.WaitForCdi;
            schedule();
        }
    }

    private Set<URI> findNamespaces(Set<URI> namespaces, Node node) {
        if (node instanceof Element || node instanceof Attr) {
            String ns = node.getNamespaceURI();
            if (ns != null && !isIgnorableAttributeNamespace(ns)) {
                namespaces.add(URI.create(ns));
            }
        }
        NamedNodeMap nnm = node.getAttributes();
        if (nnm != null) {
            for (int i = 0; i< nnm.getLength(); i++) {
                findNamespaces(namespaces, nnm.item(i));
            }
        }
        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            findNamespaces(namespaces, nl.item(i));
        }
        return namespaces;
    }

    private boolean isIgnorableAttributeNamespace(String ns) {
        return XMLConstants.RELAXNG_NS_URI.equals(ns) 
                || XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(ns)
                || XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(ns)
                || XMLConstants.W3C_XPATH_DATATYPE_NS_URI.equals(ns)
                || XMLConstants.W3C_XPATH_DATATYPE_NS_URI.equals(ns)
                || XMLConstants.XML_DTD_NS_URI.equals(ns)
                || XMLConstants.XML_NS_URI.equals(ns)
                || XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(ns);
    }
    
    /**
     * OSGi container code needs to register these outside of 
     * TransformerRegistryLoader.registerOOTBTransformers() due to the fact that 
     * descriptors cannot be parsed directly from the class path.
     */
    private void registerOOTBTransformers() throws Exception {
        Collection<ServiceReference<TransformSource>> refs = 
                _bundleContext.getServiceReferences(TransformSource.class, null);
        
        // Find all SY bundles which contain transformer definitions
        for (final ServiceReference<TransformSource> ref : refs) {
            // Create a customized transformer loader which loads classes via the bundle
            TransformerRegistryLoader loader = 
                    new TransformerRegistryLoader(_domain.getTransformerRegistry()) {
                @Override
                protected Class<?> getClass(String className) {
                    Class<?> clazz = null;
                    try {
                        clazz = ref.getBundle().loadClass(className);
                    } catch (ClassNotFoundException ex) {
                        logger.warn("Failed to load transformer class " + className + 
                                " from bundle " + ref.getBundle().getSymbolicName());
                    }
                    return clazz;
                }  
            };
            TransformSource trs = _bundleContext.getService(ref);
            InputStream tStream = null;
            
            // parse and register the transformer definitions
            try {
                tStream = trs.getTransformsURL().openStream();
                Element tConfig = new ElementPuller().pull(tStream);
                Set<URI> tNamespaces = findNamespaces(new HashSet<URI>(), tConfig);
                NamespaceHandlerSet tHandlers = _extender.getNamespaceHandlerRegistry()
                        .getNamespaceHandlers(tNamespaces, getBundle());
                TransformsModel tm = new ModelPuller<TransformsModel>(
                        new OsgiDescriptor(tHandlers)).pull(tConfig);
                
                loader.registerTransformers(tm);
            } catch (final DuplicateTransformerException e) {
                // duplicate OOTB transformers are not an error - log for visibility
                logger.debug(e.getMessage());
            } catch (Exception ex) {
                logger.warn("Failed to load transformers from bundle: " 
                        + ref.getBundle().getSymbolicName(), ex);
            } finally {
                if (tStream != null) {
                    tStream.close();
                }
                _bundleContext.ungetService(ref);
            }
        }
    }
    
    /**
     * OSGi wrapper for Descriptor which loads marshallers and schema based on 
     * registered namespace handlers.
     */
    private class OsgiDescriptor extends Descriptor {
        
        private NamespaceHandlerSet _namespaceHandlers;
        
        /**
         * Create a new OSGi wrapper Descriptor.
         * @param namespaceHandlers namespace handlers to use in descriptor
         */
        OsgiDescriptor(NamespaceHandlerSet namespaceHandlers) {
            super();
            _namespaceHandlers = namespaceHandlers;
        }
        
        @Override
        public synchronized Schema getSchema(Set<String> namespaces) {
            try {
                return _namespaceHandlers.getSchema();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public synchronized Marshaller getMarshaller(String namespace) {
            return _namespaceHandlers.getNamespaceHandler(URI.create(namespace)).createMarshaller(namespace, this);
        }
        
        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
        
        @Override
        public int hashCode() {
            return super.hashCode();
        }
        
    };
}


