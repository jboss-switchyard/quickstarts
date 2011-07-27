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

package org.switchyard.deploy.internal;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.ServiceDomain;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerRegistry;
import org.switchyard.transform.TransformerRegistryLoader;
import org.switchyard.transform.jaxb.internal.JAXBTransformerFactory;

/**
 * Abstract SwitchYard application deployment.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class AbstractDeployment {
    /**
     * Default classpath location for the switchyard configuration.
     */
    public static final String SWITCHYARD_XML = "/META-INF/switchyard.xml";
    /**
     * Parent deployment.
     */
    private AbstractDeployment _parentDeployment;
    /**
     * The Service Domain.
     */
    private ServiceDomain _serviceDomain;
    /**
     * Transform registry loaded for the deployment.
     */
    private TransformerRegistryLoader _transformerRegistryLoader;
    /**
     * SwitchYard configuration.
     */
    private SwitchYardModel _switchyardConfig;
    /**
     * The name for this deployment.
     */
    private QName _name;
    /**
     * DeploymentListener objects registered with this deployment.
     */
    private Set<DeploymentListener> _listeners = new LinkedHashSet<DeploymentListener>();

    /**
     * Automatically registered transformers (e.g. JAXB type transformers).
     */
    private List<Transformer<?, ?>> _autoRegisteredTransformers = new ArrayList<Transformer<?, ?>>();

    private static Logger _log = Logger.getLogger(AbstractDeployment.class);

    /**
     * Create a new instance of a deployment from a configuration model.
     * @param configModel switchyard config model
     */
    protected AbstractDeployment(SwitchYardModel configModel) {
        _switchyardConfig = configModel;
    }

    /**
     * Add a listener to this deployment.
     * 
     * @param listener the listener to add.
     */
    public void addDeploymentListener(DeploymentListener listener) {
        synchronized (_listeners) {
            _listeners.add(listener);
        }
    }
    
    /**
     * Remove a listener from this deployment.
     * 
     * @param listener the listener to remove.
     */
    public void removeDeploymentListener(DeploymentListener listener) {
        synchronized (_listeners) {
            _listeners.remove(listener);
        }
    }

    /**
     * Set the parent deployment.
     * <p/>
     * This must be called before calling {@link #init(org.switchyard.ServiceDomain)}.
     * @param parentDeployment The parent deployment.
     */
    public void setParentDeployment(AbstractDeployment parentDeployment) {
        this._parentDeployment = parentDeployment;
    }

    /**
     * Initialise the deployment.
     * @param appServiceDomain The ServiceDomain for the application.
     */
    public final void init(ServiceDomain appServiceDomain) {
        if (appServiceDomain == null) {
            throw new IllegalArgumentException("null 'appServiceDomain' argument.");
        }

        // initialize deployment name
        if (getConfig() != null) {
            _name = getConfig().getQName();
            if (_name == null) {
                // initialize to composite name if config name is missing
                if (getConfig().getComposite() != null) {
                    _name = getConfig().getComposite().getQName();
                }
            }
        }

        notifyListeners(new InitializingNotifier());

        try {
            _serviceDomain = appServiceDomain;
            _transformerRegistryLoader = new TransformerRegistryLoader(appServiceDomain.getTransformerRegistry());
            _transformerRegistryLoader.loadOOTBTransforms();
            
            doInit();
        } catch (RuntimeException e) {
            notifyListeners(new InitializationFailedNotifier(e));
            throw e;
        }

        notifyListeners(new InitializedNotifier());
    }

    /**
     * Start/un-pause the deployment.
     */
    public final void start() {
        notifyListeners(new StartingNotifier());
        
        try {
            doStart();
        } catch (RuntimeException e) {
            notifyListeners(new StartFailedNotifier(e));
            throw e;
        }

        notifyListeners(new StartedNotifier());
    }

    /**
     * Stop/pause the deployment.
     */
    public final void stop() {
        notifyListeners(new StoppingNotifier());
        
        try {
            doStop();
        } catch (RuntimeException e) {
            notifyListeners(new StopFailedNotifier(e));
            throw e;
        }

        notifyListeners(new StoppedNotifier());
    }

    /**
     * Destroy the deployment.
     */
    public final void destroy() {
        notifyListeners(new DestroyingNotifier());
        
        try {
            doDestroy();
        } finally {
            notifyListeners(new DestroyedNotifier());
        }
    }

    /**
     * This field is not available until after the deployment has been
     * initialized.
     * 
     * @return the name for this deployment; may be null.
     */
    public QName getName() {
        return _name;
    }

    /**
     * Get the {@link ServiceDomain} associated with the deployment.
     * @return The domain instance.
     */
    public ServiceDomain getDomain() {
        if (_parentDeployment == null) {
            return _serviceDomain;
        } else {
            return _parentDeployment.getDomain();
        }
    }

    /**
     * Get the {@link TransformerRegistryLoader} associated with the deployment.
     * @return The TransformerRegistryLoader instance.
     */
    public TransformerRegistryLoader getTransformerRegistryLoader() {
        return _transformerRegistryLoader;
    }

    /**
     * Notify the implementation to initialize itself.
     */
    protected abstract void doInit();

    /**
     * Notify the implementation to start itself.
     */
    protected abstract void doStart();

    /**
     * Notify the implementation to stop itself.
     */
    protected abstract void doStop();

    /**
     * Notify the implementation to destroy itself.
     */
    protected abstract void doDestroy();

    /**
     * @return the SwitchYard configuration for this deployment.
     */
    public SwitchYardModel getConfig() {
        return _switchyardConfig;
    }

    protected void deployAutoRegisteredTransformers(ServiceInterface serviceInterface) throws SwitchYardException {
        TransformerRegistry transformerReg = getDomain().getTransformerRegistry();

        if (serviceInterface instanceof JavaService) {
            Class<?> javaInterface = ((JavaService) serviceInterface).getJavaInterface();
            List<Transformer<?,?>> jaxbTransformers = JAXBTransformerFactory.newTransformers(javaInterface);

            for (Transformer<?,?> jaxbTransformer : jaxbTransformers) {
                if (!transformerReg.hasTransformer(jaxbTransformer.getFrom(), jaxbTransformer.getTo())) {
                    transformerReg.addTransformer(jaxbTransformer);
                    _autoRegisteredTransformers.add(jaxbTransformer);
                }
            }
        }
    }

    protected void undeployAutoRegisteredTransformers() {
        TransformerRegistry transformerReg = getDomain().getTransformerRegistry();

        for (Transformer dynamicallyAddedTransformer : _autoRegisteredTransformers) {
            transformerReg.removeTransformer(dynamicallyAddedTransformer);
        }
        _autoRegisteredTransformers.clear();
    }

    protected final void fireServiceDeployed(CompositeServiceModel serviceModel) {
        notifyListeners(new ServiceDeployedNotifier(serviceModel));
    }

    protected final void fireServiceUndeployed(QName serviceName) {
        notifyListeners(new ServiceUndeployedNotifier(serviceName));
    }

    private void notifyListeners(DeploymentEventNotifier notifier) {
        List<DeploymentListener> listeners;
        synchronized (_listeners) {
            listeners = new ArrayList<DeploymentListener>(_listeners);
        }
        for (DeploymentListener listener : listeners) {
            try {
                notifier.notify(listener);
            } catch (Exception e) {
                _log.warn("DeploymentListener threw exception during notify.", e);
            }
        }
    }

    private static interface DeploymentEventNotifier {
        public void notify(DeploymentListener listener);
    }

    private class ServiceDeployedNotifier implements DeploymentEventNotifier {
        private CompositeServiceModel _serviceModel;

        protected ServiceDeployedNotifier(CompositeServiceModel serviceModel) {
            _serviceModel = serviceModel;
        }

        public void notify(DeploymentListener listener) {
            listener.serviceDeployed(AbstractDeployment.this, _serviceModel);
        }
    }

    private class ServiceUndeployedNotifier implements DeploymentEventNotifier {
        private QName _serviceName;

        protected ServiceUndeployedNotifier(QName serviceName) {
            _serviceName = serviceName;
        }

        public void notify(DeploymentListener listener) {
            listener.serviceUndeployed(AbstractDeployment.this, _serviceName);
        }
    }

    private class InitializingNotifier implements DeploymentEventNotifier {
        public void notify(DeploymentListener listener) {
            listener.initializing(AbstractDeployment.this);
        }
    }

    private class InitializedNotifier implements DeploymentEventNotifier {
        public void notify(DeploymentListener listener) {
            listener.initialized(AbstractDeployment.this);
        }
    }

    private class InitializationFailedNotifier implements DeploymentEventNotifier {
        private Throwable _exception;

        protected InitializationFailedNotifier(Throwable exception) {
            _exception = exception;
        }

        public void notify(DeploymentListener listener) {
            listener.initializationFailed(AbstractDeployment.this, _exception);
        }
    }

    private class StartingNotifier implements DeploymentEventNotifier {
        public void notify(DeploymentListener listener) {
            listener.starting(AbstractDeployment.this);
        }
    }

    private class StartedNotifier implements DeploymentEventNotifier {
        public void notify(DeploymentListener listener) {
            listener.started(AbstractDeployment.this);
        }
    }

    private class StartFailedNotifier implements DeploymentEventNotifier {
        private Throwable _exception;

        protected StartFailedNotifier(Throwable exception) {
            _exception = exception;
        }

        public void notify(DeploymentListener listener) {
            listener.startFailed(AbstractDeployment.this, _exception);
        }
    }

    private class StoppingNotifier implements DeploymentEventNotifier {
        public void notify(DeploymentListener listener) {
            listener.stopping(AbstractDeployment.this);
        }
    }

    private class StoppedNotifier implements DeploymentEventNotifier {
        public void notify(DeploymentListener listener) {
            listener.stopped(AbstractDeployment.this);
        }
    }

    private class StopFailedNotifier implements DeploymentEventNotifier {
        private Throwable _exception;

        protected StopFailedNotifier(Throwable exception) {
            _exception = exception;
        }

        public void notify(DeploymentListener listener) {
            listener.stopFailed(AbstractDeployment.this, _exception);
        }
    }

    private class DestroyingNotifier implements DeploymentEventNotifier {
        public void notify(DeploymentListener listener) {
            listener.destroying(AbstractDeployment.this);
        }
    }

    private class DestroyedNotifier implements DeploymentEventNotifier {
        public void notify(DeploymentListener listener) {
            listener.destroyed(AbstractDeployment.this);
        }
    }

}
