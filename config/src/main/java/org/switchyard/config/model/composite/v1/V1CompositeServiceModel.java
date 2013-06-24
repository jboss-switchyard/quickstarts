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

package org.switchyard.config.model.composite.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.common.lang.Strings;
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

    private static final Logger LOGGER = Logger.getLogger(V1CompositeServiceModel.class);

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
    public ComponentModel getComponent() {
        CompositeModel composite = getComposite();
        if (composite != null) {
            String[] promote = Strings.splitTrimToNullArray(getPromote(), "/");
            if (promote.length > 0) {
                String componentName = promote[0];
                for (ComponentModel component : composite.getComponents()) {
                    if (componentName.equals(component.getName())) {
                        return component;
                    }
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentServiceModel getComponentService() {
        CompositeModel composite = getComposite();
        if (composite != null) {
            String[] promote = Strings.splitTrimToNullArray(getPromote(), "/");
            int count = promote.length;
            if (count > 0) {
                String componentName = promote[0];
                String componentServiceName = (count == 2) ? promote[1] : null;
                boolean missingComponent = true;
                for (ComponentModel component : composite.getComponents()) {
                    if (componentName.equals(component.getName())) {
                        missingComponent = false;
                        List<ComponentServiceModel> services = component.getServices();
                        if (count == 1) {
                            if (services.size() > 0) {
                                return services.get(0);
                            }
                            break;
                        } else if (count == 2) {
                            for (ComponentServiceModel service : services) {
                                if (componentServiceName.equals(service.getName())) {
                                    return service;
                                }
                            }
                            LOGGER.warn("missing component service [" + componentServiceName + "] for component [" + componentName + "]");
                        }
                    }
                }
                LOGGER.warn("missing component service for " + (missingComponent ? "missing " : "") + "component [" + componentName + "]");
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
