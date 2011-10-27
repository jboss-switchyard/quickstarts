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

import java.util.EventListener;

import javax.xml.namespace.QName;

import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.config.model.validate.ValidatesModel;

/**
 * DeploymentListener
 * 
 * Instances of classes implementing this interface can register to be notified
 * of events occurring during application deployment.
 * 
 * @author Rob Cernich
 */
public interface DeploymentListener extends EventListener {

    // lifecycle events

    /**
     * Notification that the deployment is beginning its initialization (i.e.
     * somebody has invoked
     * {@link AbstractDeployment#init(org.switchyard.ServiceDomain)}).
     * 
     * @param deployment the notifier
     */
    public void initializing(AbstractDeployment deployment);

    /**
     * Notification that the deployment has successfully completed its
     * initialization (i.e.
     * {@link AbstractDeployment#init(org.switchyard.ServiceDomain)} has
     * completed its processing).
     * 
     * @param deployment the notifier
     */
    public void initialized(AbstractDeployment deployment);

    /**
     * Notification that the deployment has failed its initialization (i.e.
     * {@link AbstractDeployment#init(org.switchyard.ServiceDomain)} erred
     * during processing).
     * 
     * @param deployment the notifier
     * @param throwable the cause of the failure
     */
    public void initializationFailed(AbstractDeployment deployment, Throwable throwable);

    /**
     * Notification that the deployment is about to start (i.e. somebody has
     * invoked {@link AbstractDeployment#start()}).
     * 
     * @param deployment the notifier
     */
    public void starting(AbstractDeployment deployment);

    /**
     * Notification that the deployment has successfully started (i.e.
     * {@link AbstractDeployment#start()} has completed successfully).
     * 
     * @param deployment the notifier
     */
    public void started(AbstractDeployment deployment);

    /**
     * Notification that a failure occurred while the deployment was starting
     * (i.e. {@link AbstractDeployment#start()} erred during processing).
     * 
     * @param deployment the notifier
     * @param throwable the cause of the failure
     */
    public void startFailed(AbstractDeployment deployment, Throwable throwable);

    /**
     * Notification that the deployment is about to stop (i.e. somebody has
     * invoked {@link AbstractDeployment#stop()}).
     * 
     * @param deployment the notifier
     */
    public void stopping(AbstractDeployment deployment);

    /**
     * Notification that the deployment has successfully stopped (i.e.
     * {@link AbstractDeployment#stop()} has completed successfully).
     * 
     * @param deployment the notifier
     */
    public void stopped(AbstractDeployment deployment);

    /**
     * Notification that a failure occurred while the deployment was stopping
     * (i.e. {@link AbstractDeployment#stop()} erred during processing).
     * 
     * @param deployment the notifier
     * @param throwable the cause of the failure
     */
    public void stopFailed(AbstractDeployment deployment, Throwable throwable);

    /**
     * Notification that the deployment is about to be destroyed (i.e. somebody
     * has invoked {@link AbstractDeployment#destroy()}).
     * 
     * @param deployment the notifier
     */
    public void destroying(AbstractDeployment deployment);

    /**
     * Notification that the deployment has been destroyed (i.e.
     * {@link AbstractDeployment#destroy()} has completed).
     * 
     * @param deployment the notifier
     */
    public void destroyed(AbstractDeployment deployment);

    // services

    /**
     * Notification that the deployment has deployed/instantiated a component
     * service. This notification is sent after the service implementation has
     * been deployed/instantiated.
     * 
     * @param deployment the notifier
     * @param componentModel the component service being deployed/instantiated
     */
    public void componentServiceDeployed(AbstractDeployment deployment, ComponentModel componentModel);

    /**
     * Notification that the deployment has undeployed a component service. This
     * notification is sent after the service has been undeployed.
     * 
     * @param deployment the notifier
     * @param serviceName the service being removed.
     */
    public void componentServiceUndeployed(AbstractDeployment deployment, QName serviceName);

    /**
     * Notification that the deployment has deployed/instantiated a service.
     * This notification is sent after all bindings have been
     * deployed/instantiated.
     * 
     * @param deployment the notifier
     * @param serviceModel the service being deployed/instantiated.
     */
    public void serviceDeployed(AbstractDeployment deployment, CompositeServiceModel serviceModel);

    /**
     * Notification that the deployment has undeployed a service. This
     * notification is sent after all bindings have been undeployed.
     * 
     * @param deployment the notifier
     * @param serviceName the name of the service being removed.
     */
    public void serviceUndeployed(AbstractDeployment deployment, QName serviceName);

    /**
     * Notification that the deployment has registered the transformers defined
     * within it.
     * 
     * @param deployment the notifier
     * @param transformers the transformers registered by the deployment.
     */
    public void transformersRegistered(AbstractDeployment deployment, TransformsModel transformers);

    /**
     * Notification that the deployment has registered the validators defined
     * within it.
     * 
     * @param deployment the notifier
     * @param validators the validators registered by the deployment.
     */
    public void validatorsRegistered(AbstractDeployment deployment, ValidatesModel validators);

}
