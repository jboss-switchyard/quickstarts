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

import org.switchyard.common.lang.Strings;
import org.switchyard.config.ConfigLogger;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.InterfaceModel;
import org.switchyard.config.model.composite.SCANamespace;

/**
 * A version 1 CompositeReferenceModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1CompositeReferenceModel extends BaseNamedModel implements CompositeReferenceModel {

    private List<BindingModel> _bindings = new ArrayList<BindingModel>();
    private InterfaceModel _interface;

    /**
     * Constructs a new V1CompositeReferenceModel.
     */
    public V1CompositeReferenceModel() {
        super(SCANamespace.DEFAULT.uri(), CompositeReferenceModel.REFERENCE);
        setMultiplicity(CompositeReferenceModel.DEFAULT_MULTIPLICITY);
    }

    /**
     * Constructs a new V1CompositeReferenceModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1CompositeReferenceModel(Configuration config, Descriptor desc) {
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
    public List<ComponentReferenceModel> getComponentReferences() {
        List<ComponentReferenceModel> list = new ArrayList<ComponentReferenceModel>();
        CompositeModel composite = getComposite();
        if (composite != null) {
            String[] promotes = Strings.splitTrimToNullArray(getPromote(), " ");
            for (String promote : promotes) {
                String[] names =  Strings.splitTrimToNullArray(promote, "/");
                int namesCount = names.length;
                if (namesCount > 0) {
                    String componentName = names[0];
                    String componentReferenceName = (namesCount == 2) ? names[1] : null;
                    boolean componentMissing = true;
                    componentLoop: for (ComponentModel component : composite.getComponents()) {
                        if (componentName.equals(component.getName())) {
                            List<ComponentReferenceModel> componentReferences = component.getReferences();
                            if (namesCount == 1) {
                                if (componentReferences.size() > 0) {
                                    ComponentReferenceModel componentReference = componentReferences.iterator().next();
                                    if (componentReference != null) {
                                        list.add(componentReference);
                                        componentMissing = false;
                                        break componentLoop;
                                    }
                                }
                            } else if (namesCount == 2) {
                                for (ComponentReferenceModel componentReference : componentReferences) {
                                    if (componentReferenceName.equals(componentReference.getName())) {
                                        list.add(componentReference);
                                        componentMissing = false;
                                        break componentLoop;
                                    }
                                }
                            }
                        }
                    }
                    if (componentMissing) {
                        ConfigLogger.ROOT_LOGGER.missingComponentReference((componentReferenceName != null ? componentReferenceName : ""), componentName);
                    }
                }
            }
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPromote() {
        return getModelAttribute(CompositeReferenceModel.PROMOTE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompositeReferenceModel setPromote(String promote) {
        setModelAttribute(CompositeReferenceModel.PROMOTE, promote);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMultiplicity() {
        return getModelAttribute(CompositeReferenceModel.MULTIPLICITY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompositeReferenceModel setMultiplicity(String multiplicity) {
        setModelAttribute(CompositeReferenceModel.MULTIPLICITY, multiplicity);
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
    public synchronized CompositeReferenceModel addBinding(BindingModel binding) {
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
    public CompositeReferenceModel setInterface(InterfaceModel interfaze) {
        setChildModel(interfaze);
        _interface = interfaze;
        return this;
    }

}
