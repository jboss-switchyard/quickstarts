/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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

package org.switchyard.config.model.composite.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.common.lang.Strings;
import org.switchyard.config.ConfigLogger;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.ExtensionsModel;
import org.switchyard.config.model.composite.InterfaceModel;

/**
 * A version 1 CompositeServiceModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1CompositeServiceModel extends BaseNamedModel implements CompositeServiceModel {

    private List<BindingModel> _bindings = new ArrayList<BindingModel>();
    private InterfaceModel _interface;
    private ExtensionsModel _extensions;

    /**
     * Constructs a new V1CompositeServiceModel.
     */
    public V1CompositeServiceModel() {
        super(new QName(CompositeModel.DEFAULT_NAMESPACE, CompositeServiceModel.SERVICE));
    }

    /**
     * Constructs a new V1CompositeServiceModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1CompositeServiceModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration binding_config : config.getChildrenStartsWith(BindingModel.BINDING)) {
            BindingModel binding = (BindingModel)readModel(binding_config);
            if (binding != null) {
                _bindings.add(binding);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompositeModel getComposite() {
        return (CompositeModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentServiceModel getComponentService() {
        CompositeModel composite = getComposite();
        if (composite != null) {
            String[] promotes = Strings.splitTrimToNullArray(getPromote(), " ");
            switch (promotes.length) {
                case 0:
                    break;
                case 1:
                    String[] names =  Strings.splitTrimToNullArray(promotes[0], "/");
                    int namesCount = names.length;
                    if (namesCount > 0) {
                        String componentName = names[0];
                        String componentServiceName = (namesCount == 2) ? names[1] : null;
                        for (ComponentModel component : composite.getComponents()) {
                            if (componentName.equals(component.getName())) {
                                List<ComponentServiceModel> componentServices = component.getServices();
                                if (namesCount == 1) {
                                    if (componentServices.size() > 0) {
                                        ComponentServiceModel componentService = componentServices.iterator().next();
                                        if (componentService != null) {
                                            return componentService;
                                        }
                                    }
                                } else if (namesCount == 2) {
                                    for (ComponentServiceModel componentService : componentServices) {
                                        if (componentServiceName.equals(componentService.getName())) {
                                            if (componentService != null) {
                                                return componentService;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        ConfigLogger.ROOT_LOGGER.missingComponentService((componentServiceName != null ? componentServiceName : ""), componentName);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("A composite service can only promote one component service.");
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPromote() {
        return getModelAttribute(CompositeServiceModel.PROMOTE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompositeServiceModel setPromote(String promote) {
        setModelAttribute(CompositeServiceModel.PROMOTE, promote);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<BindingModel> getBindings() {
        return Collections.unmodifiableList(_bindings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized CompositeServiceModel addBinding(BindingModel binding) {
        addChildModel(binding);
        _bindings.add(binding);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InterfaceModel getInterface() {
        if (_interface == null) {
            _interface = (InterfaceModel)getFirstChildModelStartsWith(InterfaceModel.INTERFACE);
        }
        return _interface;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompositeServiceModel setInterface(InterfaceModel interfaze) {
        setChildModel(interfaze);
        _interface = interfaze;
        return this;
    }

    @Override
    public ExtensionsModel getExtensions() {
        if (_extensions == null) {
            _extensions = (ExtensionsModel) getFirstChildModel(ExtensionsModel.EXTENSIONS);
        }
        return _extensions;
    }

    @Override
    public CompositeServiceModel setExtensions(ExtensionsModel extensions) {
        setChildModel(extensions);
        _extensions = extensions;
        return this;
    }
}
