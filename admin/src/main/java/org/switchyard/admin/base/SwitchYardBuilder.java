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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.admin.Binding;
import org.switchyard.admin.ComponentReference;
import org.switchyard.admin.ComponentService;
import org.switchyard.admin.Transformer;
import org.switchyard.config.OutputKey;
import org.switchyard.config.model.TypedModel;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.TransformsModel;
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
    public void componentServiceDeployed(AbstractDeployment deployment, ComponentModel componentModel) {
        final QName applicationName = deployment.getName();
        if (applicationName == null) {
            _log.warn("componentServiceDeployed() received deployment with null application name.");
            return;
        }
        final BaseApplication application = (BaseApplication) _switchYard.getApplication(applicationName);
        if (componentModel.getServices().size() != 1) {
            _log.warn("componentServiceDeployed() received component with service count != 1.");
            return;
        }
        final ComponentServiceModel componentServiceModel = componentModel.getServices().get(0);
        final QName name = componentServiceModel.getQName();
        final String interfaceName = getInterfaceName(componentServiceModel.getInterface());
        final String implementation = getComponentImplementationType(componentModel);
        final String implementationConfiguration = getComponentImplementationConfiguration(componentModel);
        final List<ComponentReference> references = new ArrayList<ComponentReference>();
        for (ComponentReferenceModel referenceModel : componentModel.getReferences()) {
            references.add(new BaseComponentReference(referenceModel.getQName(), getInterfaceName(referenceModel
                    .getInterface())));
        }
        application.addComponentService(new BaseComponentService(name, implementation, implementationConfiguration,
                interfaceName, application, references));
    }

    @Override
    public void componentServiceUndeployed(AbstractDeployment deployment, QName serviceName) {
        final QName applicationName = deployment.getName();
        if (applicationName == null) {
            _log.warn("componentServiceUndeployed() received deployment with null application name.");
            return;
        }
        final BaseApplication application = (BaseApplication) _switchYard.getApplication(applicationName);
        application.removeComponentService(serviceName);
    }

    @Override
    public void serviceDeployed(AbstractDeployment deployment, CompositeServiceModel serviceModel) {
        final QName applicationName = deployment.getName();
        if (applicationName == null) {
            _log.warn("serviceDeployed() received deployment with null application name.");
            return;
        }
        final BaseApplication application = (BaseApplication) _switchYard.getApplication(applicationName);
        final QName name = serviceModel.getQName();
        final String interfaceName = getInterfaceName(serviceModel.getInterface());
        final ComponentService promotedService = getPromotedService(application, serviceModel);
        final List<Binding> gateways = new ArrayList<Binding>();
        for (BindingModel bindingModel : serviceModel.getBindings()) {
            String bindingConfiguration = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                bindingModel.getModelConfiguration().write(baos, OutputKey.EXCLUDE_XML_DECLARATION);
                bindingConfiguration = baos.toString();
            } catch (IOException e) {
                // FIXME: do we need to log this?
                _log.error("Could not retrieve binding configuration as string.", e);
            } finally {
                try {
                    baos.close();
                } catch (IOException e) {
                    // prevent code style error
                    e.getMessage();
                }
            }
            gateways.add(new BaseBinding(bindingModel.getType(), bindingConfiguration));
        }
        BaseService service = new BaseService(name, interfaceName, application, promotedService, gateways);

        application.addService(service);
    }

    @Override
    public void serviceUndeployed(AbstractDeployment deployment, QName serviceName) {
        final QName applicationName = deployment.getName();
        if (applicationName == null) {
            _log.warn("serviceUndeployed() received deployment with null application name.");
            return;
        }
        final BaseApplication application = (BaseApplication) _switchYard.getApplication(applicationName);
        application.removeService(serviceName);
    }

    @Override
    public void transformersRegistered(AbstractDeployment deployment, TransformsModel transformsModel) {
        if (transformsModel == null) {
            return;
        }

        final QName applicationName = deployment.getName();
        if (applicationName == null) {
            _log.warn("serviceUndeployed() received deployment with null application name.");
            return;
        }

        final List<TransformModel> transformModels = transformsModel.getTransforms();
        if (transformModels == null) {
            return;
        }

        final BaseApplication application = (BaseApplication) _switchYard.getApplication(applicationName);
        final List<Transformer> transformers = new ArrayList<Transformer>(transformModels.size());
        for (TransformModel transformModel : transformModels) {
            String type;
            if (transformModel instanceof TypedModel) {
                type = ((TypedModel)transformModel).getType();
            } else {
                type = null;
            }
            transformers.add(new BaseTransformer(transformModel.getFrom(), transformModel.getTo(), type));
        }
        application.setTransformers(transformers);
    }

    private ComponentService getPromotedService(BaseApplication application, CompositeServiceModel compositeService) {
        ComponentServiceModel componentServiceModel = compositeService.getComponentService();
        if (componentServiceModel == null) {
            return null;
        }
        return application.getComponentService(componentServiceModel.getQName());
    }

    private String getInterfaceName(InterfaceModel interfaceModel) {
        if (interfaceModel == null) {
            return null;
        }
        return interfaceModel.getInterface();
    }

    private String getComponentImplementationType(ComponentModel componentModel) {
        ComponentImplementationModel implementationModel = componentModel.getImplementation();
        if (implementationModel == null) {
            return null;
        }
        return implementationModel.getType();
    }

    private String getComponentImplementationConfiguration(ComponentModel componentModel) {
        ComponentImplementationModel implementationModel = componentModel.getImplementation();
        if (implementationModel == null) {
            return null;
        }
        String configuration = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            implementationModel.getModelConfiguration().write(baos, OutputKey.EXCLUDE_XML_DECLARATION);
            configuration = baos.toString();
        } catch (IOException e) {
            // FIXME: do we need to log this?
            _log.error("Could not retrieve implementation configuration as string.", e);
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                // prevent code style error
                e.getMessage();
            }
        }
        return configuration;
    }

}
