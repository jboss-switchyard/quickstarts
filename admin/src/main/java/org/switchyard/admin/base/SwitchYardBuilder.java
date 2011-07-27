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
package org.switchyard.admin.base;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.admin.Component;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.deploy.internal.DeploymentListener;

/**
 * SwitchYardBuilder
 * 
 * {@link DeploymentListener} implementation which builds a
 * {@link org.switchyard.admin.SwitchYard} model using notifications from an
 * {@link AbstractDeployment} object.
 * 
 * @author Rob Cernich
 */
public class SwitchYardBuilder implements DeploymentListener {

    private static Logger _log = Logger.getLogger(SwitchYardBuilder.class);

    private BaseSwitchYard _switchYard;

    /**
     * Create a new SwitchYardBuilder.
     * 
     * @param switchYard the {@link BaseSwitchYard} instance used to process
     *            deployment notifications.
     */
    public SwitchYardBuilder(BaseSwitchYard switchYard) {
        _switchYard = switchYard;
    }

    @Override
    public void initializing(AbstractDeployment deployment) {
        final QName applicationName = deployment.getName();
        if (applicationName == null) {
            _log.warn("initializing() received deployment with null application name.");
            return;
        }
        _switchYard.addApplication(new BaseApplication(_switchYard, applicationName));
    }

    @Override
    public void initialized(AbstractDeployment deployment) {
    }

    @Override
    public void initializationFailed(AbstractDeployment deployment, Throwable throwable) {
        final QName applicationName = deployment.getName();
        if (applicationName == null) {
            _log.warn("initializationFailed() received deployment with null application name.");
            return;
        }
        _switchYard.removeApplication(applicationName);
    }

    @Override
    public void starting(AbstractDeployment deployment) {
    }

    @Override
    public void started(AbstractDeployment deployment) {
    }

    @Override
    public void startFailed(AbstractDeployment deployment, Throwable throwable) {
    }

    @Override
    public void stopping(AbstractDeployment deployment) {
    }

    @Override
    public void stopped(AbstractDeployment deployment) {
    }

    @Override
    public void stopFailed(AbstractDeployment deployment, Throwable throwable) {
    }

    @Override
    public void destroying(AbstractDeployment deployment) {
    }

    @Override
    public void destroyed(AbstractDeployment deployment) {
        final QName applicationName = deployment.getName();
        if (applicationName == null) {
            _log.warn("destroyed() received deployment with null application name.");
            return;
        }
        _switchYard.removeApplication(applicationName);
    }

    @Override
    public void serviceDeployed(AbstractDeployment deployment, CompositeServiceModel serviceModel) {
        final QName applicationName = deployment.getName();
        if (applicationName == null) {
            _log.warn("serviceDeployed() received deployment with null application name.");
            return;
        }
        final BaseApplication application = (BaseApplication) _switchYard.findApplication(applicationName);
        final QName name = serviceModel.getQName();
        final String interfaceName = getServiceInterface(serviceModel);
        final Component implementation = getServiceImplementation(serviceModel);
        final List<Component> gateways = new ArrayList<Component>();
        for (BindingModel binding : serviceModel.getBindings()) {
            Component component = _switchYard.findComponent(binding.getType());
            if (component != null) {
                gateways.add(component);
            }
        }
        BaseService service = new BaseService(name, interfaceName, application, implementation, gateways);

        application.addService(service);
    }

    @Override
    public void serviceUndeployed(AbstractDeployment deployment, QName serviceName) {
        final QName applicationName = deployment.getName();
        if (applicationName == null) {
            _log.warn("serviceUndeployed() received deployment with null application name.");
            return;
        }
        final BaseApplication application = (BaseApplication) _switchYard.findApplication(applicationName);
        application.removeService(serviceName);
    }

    private Component getServiceImplementation(CompositeServiceModel compositeService) {
        ComponentModel componentModel = compositeService.getComponent();
        if (componentModel == null) {
            return null;
        }
        ComponentImplementationModel componentImplementationModel = componentModel.getImplementation();
        if (componentImplementationModel == null) {
            return null;
        }
        return _switchYard.findComponent(componentImplementationModel.getType());
    }

    private String getServiceInterface(CompositeServiceModel compositeService) {
        InterfaceModel interfaceModel = compositeService.getInterface();
        if (interfaceModel != null) {
            return interfaceModel.getInterface();
        }
        ComponentServiceModel componentServiceModel = compositeService.getComponentService();
        if (componentServiceModel == null) {
            return null;
        }
        if (componentServiceModel.getInterface() == null) {
            return null;
        }
        return componentServiceModel.getInterface().getInterface();
    }

}
