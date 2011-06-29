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

import org.switchyard.ServiceDomain;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerRegistry;
import org.switchyard.transform.TransformerRegistryLoader;
import org.switchyard.transform.jaxb.internal.JAXBTransformerFactory;

import java.util.ArrayList;
import java.util.List;

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
     * Automatically registered transformers (e.g. JAXB type transformers).
     */
    private List<Transformer<?, ?>> _autoRegisteredTransformers = new ArrayList<Transformer<?, ?>>();

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
     * Initialise the deployment.
     * @param appServiceDomain The ServiceDomain for the application.
     */
    public void init(ServiceDomain appServiceDomain) {
        if (appServiceDomain == null) {
            throw new IllegalArgumentException("null 'appServiceDomain' argument.");
        }
        _serviceDomain = appServiceDomain;
        _transformerRegistryLoader = new TransformerRegistryLoader(appServiceDomain.getTransformerRegistry());
        _transformerRegistryLoader.loadOOTBTransforms();
    }

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

    protected SwitchYardModel getConfig() {
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
}
