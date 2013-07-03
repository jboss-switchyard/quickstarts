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

import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.ServiceDomain;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.Lifecycle;
import org.switchyard.transform.internal.TransformerRegistryLoader;
import org.switchyard.validate.internal.ValidatorRegistryLoader;

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
     * Deployment classloader property name.
     */
    public static final String CLASSLOADER_PROPERTY = 
            "org.switchyard.deployment.DeploymentClassLoader";

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
     * Validate registry loaded for the deployment.
     */
    private ValidatorRegistryLoader _validatorRegistryLoader;
    /**
     * SwitchYard configuration.
     */
    private SwitchYardModel _switchyardConfig;
    /**
     * The name for this deployment.
     */
    private QName _name;

    /**
     * Flag to indicate whether or not deployment should fail if an activator is not available.
     */
    private boolean _failOnMissingActivator = true;

    /**
     * Create a new instance of a deployment from a configuration model.
     * @param configModel switchyard config model
     */
    protected AbstractDeployment(SwitchYardModel configModel) {
        _switchyardConfig = configModel;
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
     * Should the deployment fail on a missing Activator.
     * @return {@code true} if the deployment should fail, otherwise {@code false}.
     */
    public boolean failOnMissingActivator() {
        return _failOnMissingActivator;
    }

    /**
     * Set whether or not the deployment should fail on a missing Activator.
     * @param failOnMissingActivator {@code true} if the deployment should fail, otherwise {@code false}.
     */
    public void setFailOnMissingActivator(boolean failOnMissingActivator) {
        this._failOnMissingActivator = failOnMissingActivator;
    }

    /**
     * Initialise the deployment.
     * @param appServiceDomain The ServiceDomain for the application.
     * @param activators The list of SwitchYard component activators.
     */
    public final void init(ServiceDomain appServiceDomain, List<Activator> activators) {
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
        
        _serviceDomain = appServiceDomain;
        _serviceDomain.setProperty(CLASSLOADER_PROPERTY, Classes.getTCCL());
        _transformerRegistryLoader = new TransformerRegistryLoader(appServiceDomain.getTransformerRegistry());
        _transformerRegistryLoader.loadOOTBTransforms();
        
        _validatorRegistryLoader = new ValidatorRegistryLoader(appServiceDomain.getValidatorRegistry());
        _validatorRegistryLoader.loadOOTBValidates();
        
        doInit(activators);
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
     * Get the {@link ValidatorRegistryLoader} associated with the deployment.
     * @return The ValidatorRegistryLoader instance.
     */
    public ValidatorRegistryLoader getValidatorRegistryLoader() {
        return _validatorRegistryLoader;
    }
    
    /**
     * Initialize the deployment
     */
    protected abstract void doInit(List<Activator> activators);

    /**
     * Start/un-pause the deployment.
     */
    public abstract void start();

    /**
     * Stop/pause the deployment.
     */
    public abstract void stop();

    /**
     * Destroy the deployment.
     */
    public abstract void destroy();

    /**
     * Retrieves the Lifecycle for the specified gateway.
     * 
     * @param serviceName the name of the service or reference providing the gateway.
     * @param bindingName the name of the gateway (binding)
     * @return the Lifecycle object associated with the gateway.
     */
    public abstract Lifecycle getGatwayLifecycle(QName serviceName, String bindingName);

    /**
     * @return the SwitchYard configuration for this deployment.
     */
    public SwitchYardModel getConfig() {
        return _switchyardConfig;
    }
}
