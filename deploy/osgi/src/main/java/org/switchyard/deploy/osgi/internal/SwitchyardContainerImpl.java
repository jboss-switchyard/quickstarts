/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
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

package org.switchyard.deploy.osgi.internal;

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
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.Component;
import org.switchyard.deploy.internal.Deployment;
import org.switchyard.deploy.osgi.ComponentRegistry;
import org.switchyard.deploy.osgi.NamespaceHandler;
import org.switchyard.deploy.osgi.NamespaceHandlerSet;
import org.switchyard.deploy.osgi.SwitchyardContainer;
import org.switchyard.deploy.osgi.SwitchyardEvent;
import org.switchyard.deploy.osgi.base.SimpleExtension;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
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

/**
 */
public class SwitchyardContainerImpl extends SimpleExtension
        implements NamespaceHandlerSet.Listener, ComponentRegistry.Listener, Runnable, SwitchyardContainer {

    public static final String CONTAINER_SYMBOLIC_NAME_PROPERTY = "switchyard.container.symbolicname";
    public static final String CONTAINER_VERSION_PROPERTY = "switchyard.container.version";

    private static final Logger logger = LoggerFactory.getLogger(SwitchyardExtender.class);

    public enum State {
        Unknown,
        WaitForCdi,
        WaitForNamespaceHandlers,
        WaitForComponents,
        Created,
        Failed,
    }


    private final SwitchyardExtender extender;
    private final Bundle extenderBundle;
    private State state = State.Unknown;
    private Element xml;
    private SwitchYardModel model;
    private Set<URI> namespaces;
    private NamespaceHandlerSet nhs;
    private Set<String> types;
    private ServiceDomain domain;
    private Deployment deployment;
    private final AtomicBoolean scheduled = new AtomicBoolean();
    private final AtomicBoolean destroyed = new AtomicBoolean(false);
    private final ExecutorService executors;
    private ServiceRegistration<SwitchyardContainer> registration;
    private ServiceTracker cdiContainerTracker;
    private Object cdiContainer;

    public SwitchyardContainerImpl(SwitchyardExtender extender, Bundle bundle, ExecutorService executor) {
        super(bundle);
        this.extender = extender;
        this.extenderBundle = extender.getBundleContext().getBundle();
        this.executors = executor != null ? new ExecutorServiceWrapper(executor) : null;
    }

    @Override
    protected Object getLock() {
        return scheduled;
    }

    public void schedule() {
        if (scheduled.compareAndSet(false, true) && !destroyed.get()) {
            executors.submit(this);
        }
    }

    public void run() {
        scheduled.set(false);
        synchronized (scheduled) {
            doStart();
        }
    }

    @Override
    protected void doStart() {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(bundle.adapt(BundleWiring.class).getClassLoader());
            for (;;) {
                if (destroyed.get()) {
                    return;
                }
                if (bundle.getState() != Bundle.ACTIVE && bundle.getState() != Bundle.STARTING) {
                    return;
                }
                if (bundle.getBundleContext() != bundleContext) {
                    return;
                }
                logger.debug("Running switchyard container for bundle {} in state {}", bundle.getSymbolicName(), state);
                switch (state) {
                    case Unknown: {
                        dispatch(SwitchyardEvent.CREATING);
                        boolean needsCdi = false;
                        List<BundleWire> wires = bundle.adapt(BundleWiring.class).getRequiredWires("osgi.extender");
                        for (BundleWire wire : wires) {
                            String filterStr = wire.getRequirement().getDirectives().get("filter");
                            Filter filter = FrameworkUtil.createFilter(filterStr);
                            Dictionary<String, Object> props = new Hashtable<String, Object>();
                            props.put("osgi.extender", "pax.cdi");
                            needsCdi = filter.match(props);
                        }
                        if (needsCdi) {
                            String filter = "(&(objectClass=org.ops4j.pax.cdi.spi.CdiContainer)(bundleId=" + bundle.getBundleId() + "))";
                            cdiContainerTracker = new ServiceTracker(bundleContext, FrameworkUtil.createFilter(filter), new ServiceTrackerCustomizer() {
                                @Override
                                public Object addingService(ServiceReference reference) {
                                    Object obj = bundleContext.getService(reference);
                                    schedule();
                                    return obj;
                                }
                                @Override
                                public void modifiedService(ServiceReference reference, Object service) {
                                }
                                @Override
                                public void removedService(ServiceReference reference, Object service) {
                                    bundleContext.ungetService(reference);
                                    enterGracePeriod();
                                }
                            });
                            cdiContainerTracker.open();
                        }
                        state = State.WaitForCdi;
                        break;
                    }
                    case WaitForCdi: {
                        if (cdiContainerTracker != null) {
                            cdiContainer = cdiContainerTracker.getService();
                            if (cdiContainer == null) {
                                String filter = "(&(objectClass=org.ops4j.pax.cdi.spi.CdiContainer)(bundleId=" + bundle.getBundleId() + "))";
                                dispatch(SwitchyardEvent.GRACE_PERIOD, Collections.singleton(filter));
                                return;
                            }
                        }
                        if (nhs == null) {
                            URL configUrl = getBundle().getResource(SwitchyardExtender.SWITCHYARD_XML);
                            InputStream configStream = configUrl.openStream();
                            try {
                                xml = new ElementPuller().pull(configStream);
                            } finally {
                                configStream.close();
                            }
                            namespaces = findNamespaces(new HashSet<URI>(), xml);
                            nhs = extender.getNamespaceHandlerRegistry().getNamespaceHandlers(namespaces, getBundle());
                            nhs.addListener(this);
                        }
                        state = State.WaitForNamespaceHandlers;
                        break;
                    }
                    case WaitForNamespaceHandlers: {
                        List<String> missing = new ArrayList<String>();
                        List<URI> missingURIs = new ArrayList<URI>();
                        for (URI ns : namespaces) {
                            if (nhs.getNamespaceHandler(ns) == null) {
                                missing.add("(&(" + Constants.OBJECTCLASS + "=" + NamespaceHandler.class.getName() + ")(" + NamespaceHandler.NAMESPACES + "=" + ns + "))");
                                missingURIs.add(ns);
                                dispatch(SwitchyardEvent.GRACE_PERIOD, missing);
                            }
                        }
                        if (missing.size() > 0) {
                            logger.info("Bundle {} is waiting for namespace handlers {}", getBundle().getSymbolicName(), missingURIs);
                            return;
                        }
                        Descriptor descriptor = new Descriptor() {
                            @Override
                            public synchronized Schema getSchema(Set<String> namespaces) {
                                try {
                                    return nhs.getSchema();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public synchronized Marshaller getMarshaller(String namespace) {
                                return nhs.getNamespaceHandler(URI.create(namespace)).createMarshaller(namespace, this);
                            }
                        };
                        model = new ModelPuller<SwitchYardModel>(descriptor).pull(xml);
                        types = new HashSet<String>();
                        for (CompositeReferenceModel reference : model.getComposite().getReferences()) {
                            for (BindingModel binding : reference.getBindings()) {
                                types.add(binding.getType());
                            }
                        }
                        for (ComponentModel component : model.getComposite().getComponents()) {
                            ComponentImplementationModel impl = component.getImplementation();
                            if (impl == null) {
                                throw new IllegalStateException("Component implementation should not be null");
                            }
                            types.add(impl.getType());
                        }
                        for (CompositeServiceModel service : model.getComposite().getServices()) {
                            for (BindingModel binding : service.getBindings()) {
                                types.add(binding.getType());
                            }
                        }
                        extender.getComponentRegistry().addListener(this);
                        state = State.WaitForComponents;
                        break;
                    }
                    case WaitForComponents: {
                        List<Component> components = new ArrayList<Component>();
                        List<String> missingTypes = new ArrayList<String>();
                        for (String type : types) {
                            Component component = extender.getComponentRegistry().getComponent(type);
                            if (component == null) {
                                missingTypes.add(type);
                            } else {
                                components.add(component);
                            }
                        }
                        if (!missingTypes.isEmpty()) {
                            logger.info("Bundle {} is waiting for components {}", getBundle().getSymbolicName(), missingTypes);
                            dispatch(SwitchyardEvent.GRACE_PERIOD, missingTypes);
                            return;
                        }

                        ClassLoader oldTccl = Thread.currentThread().getContextClassLoader();
                        ClassLoader newTccl = oldTccl;
                        if (cdiContainer != null) {
                            try {
                                Method method = cdiContainer.getClass().getMethod("getContextClassLoader");
                                newTccl = (ClassLoader) method.invoke(cdiContainer);
                            } catch (Throwable t) {
                                // Ignore
                            }
                        }
                        try {
                            Thread.currentThread().setContextClassLoader(newTccl);

                            domain = extender.getDomainManager().createDomain(getBundleContext(), model.getQName(), model);
                            List<Activator> activators = new ArrayList<Activator>();
                            for (Component component : components) {
                                activators.add(component.createActivator(domain));
                            }
                            deployment = new Deployment(model);
                            deployment.init(domain, activators);
                            deployment.start();
                        } finally {
                            Thread.currentThread().setContextClassLoader(oldTccl);
                        }
                        // Register the BlueprintContainer in the OSGi registry
                        int bs = bundle.getState();
                        if (registration == null && (bs == Bundle.ACTIVE || bs == Bundle.STARTING)) {
                            Dictionary<String, Object> props = new Hashtable<String, Object>();
                            props.put(CONTAINER_SYMBOLIC_NAME_PROPERTY, bundle.getSymbolicName());
                            props.put(CONTAINER_VERSION_PROPERTY, bundle.getVersion());
                            registration = bundleContext.registerService(SwitchyardContainer.class, this, props);
                        }
                        dispatch(SwitchyardEvent.CREATED);
                        state = State.Created;
                        break;
                    }
                    case Created:
                    case Failed:
                        return;
                }
            }
        } catch (Throwable t) {
            try {
                state = State.Failed;
                logger.error("Unable to start switchyard for bundle " + getBundle().getSymbolicName(), t);
                dispatch(SwitchyardEvent.FAILURE, t);
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
        dispatch(SwitchyardEvent.DESTROYING);
        executors.shutdownNow();
        try {
            if (registration != null) {
                registration.unregister();
            }
        } catch (Throwable t) {
            logger.debug("Error unregistering Switchyard container", t);
        }
        if (cdiContainerTracker != null) {
            cdiContainerTracker.close();
            cdiContainerTracker = null;
        }
        if (nhs != null) {
            nhs.removeListener(this);
            nhs.destroy();
            nhs = null;
        }
        try {
            executors.awaitTermination(5 * 60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.debug("Interrupted waiting for executor to shut down");
        }
        destroyDeployment();
        dispatch(SwitchyardEvent.DESTROYED);
        logger.debug("Switchyard container destroyed: {}", this.bundleContext);
    }

    private void dispatch(int type) {
        dispatch(new SwitchyardEvent(type, getBundle(), extenderBundle));
    }

    private void dispatch(int type, Collection<String> deps) {
        String[] depsArray = deps.toArray(new String[deps.size()]);
        dispatch(new SwitchyardEvent(type, getBundle(), extenderBundle, depsArray));
    }

    private void dispatch(int type, Throwable t) {
        dispatch(new SwitchyardEvent(type, getBundle(), extenderBundle, t));
    }

    private void dispatch(SwitchyardEvent event) {
        extender.getEventDispatcher().switchyardEvent(event);
    }

    @Override
    public void namespaceHandlerRegistered(URI uri) {
        if (nhs.isComplete()) {
            schedule();
        }
    }

    @Override
    public void namespaceHandlerUnregistered(URI uri) {
        if (!nhs.isComplete()) {
            enterGracePeriod();
        }
    }

    @Override
    public void componentRegistered(String type) {
        if (types != null && types.contains(type)) {
            schedule();
        }
    }

    @Override
    public void componentUnregistered(String type) {
        if (types != null && types.contains(type)) {
            enterGracePeriod();
        }
    }

    private void destroyDeployment() {
        extender.getComponentRegistry().removeListener(this);
        if (deployment != null) {
            deployment.stop();
            deployment.destroy();
            deployment = null;
        }
        if (domain != null) {
            domain.destroy();
            domain = null;
        }
    }

    private void enterGracePeriod() {
        synchronized (scheduled) {
            if (destroyed.get()) {
                return;
            }
            try {
                destroyDeployment();
            } catch (Exception e) {
                logger.error("Error while stopping switchyard", e);
            }
            state = State.WaitForCdi;
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
        if(nnm!=null){
            for(int i = 0; i< nnm.getLength() ; i++){
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
        return XMLConstants.RELAXNG_NS_URI.equals(ns) ||
                XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(ns) ||
                XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(ns) ||
                XMLConstants.W3C_XPATH_DATATYPE_NS_URI.equals(ns) ||
                XMLConstants.W3C_XPATH_DATATYPE_NS_URI.equals(ns) ||
                XMLConstants.XML_DTD_NS_URI.equals(ns) ||
                XMLConstants.XML_NS_URI.equals(ns) ||
                XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(ns);
    }

}
