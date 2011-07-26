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

import org.switchyard.common.lang.Strings;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.InterfaceModel;

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
        super(new QName(CompositeModel.DEFAULT_NAMESPACE, CompositeReferenceModel.REFERENCE));
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
    public ComponentReferenceModel getComponentReference() {
        CompositeModel composite = getComposite();
        if (composite != null) {
            String[] promote = Strings.splitTrimToNullArray(getPromote(), "/");
            int count = promote.length;
            if (count > 0) {
                String componentName = promote[0];
                String componentReferenceName = (count == 2) ? promote[1] : null;
                boolean missingComponent = true;
                for (ComponentModel component : composite.getComponents()) {
                    if (componentName.equals(component.getName())) {
                        missingComponent = false;
                        List<ComponentReferenceModel> references = component.getReferences();
                        if (count == 1) {
                            if (references.size() > 0) {
                                return references.get(0);
                            }
                            break;
                        } else if (count == 2) {
                            for (ComponentReferenceModel reference : references) {
                                if (componentReferenceName.equals(reference.getName())) {
                                    return reference;
                                }
                            }
                            throw new IllegalArgumentException("missing component reference [" + componentReferenceName + "] for component [" + componentName + "]");
                        }
                    }
                }
                throw new IllegalArgumentException("missing component reference for " + (missingComponent ? "missing " : "") + "component [" + componentName + "]");
            }
        }
        return null;
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
