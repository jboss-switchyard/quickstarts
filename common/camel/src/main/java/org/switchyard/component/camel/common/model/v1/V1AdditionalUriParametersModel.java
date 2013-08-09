/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.camel.common.model.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.switchyard.component.camel.common.model.AdditionalUriParametersModel;
import org.switchyard.component.camel.common.model.ParameterModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 1 AdditionalUriParametersModel.
 */
public class V1AdditionalUriParametersModel extends BaseModel implements AdditionalUriParametersModel {

    private List<ParameterModel> _parameters = new ArrayList<ParameterModel>();

    /**
     * 
     * Create a new AdditionalUriParametersModel.
     * 
     * @param namespace Namespace name
     */
    public V1AdditionalUriParametersModel(String namespace) {
        super(ADDITIONAL_URI_PARAMETERS, namespace);
    }

    /**
     * Create a AdditionalUriParametersModel from the specified configuration
     * and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1AdditionalUriParametersModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration parameter_config : config.getChildren(ParameterModel.PARAMETER)) {
            ParameterModel parameter = new V1ParameterModel(parameter_config, desc);
            if (parameter != null) {
                _parameters.add(parameter);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<ParameterModel> getParameters() {
        return Collections.unmodifiableList(_parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized AdditionalUriParametersModel addParameter(ParameterModel parameter) {
        addChildModel(parameter);
        _parameters.add(parameter);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ParameterModel removeParameter(String parameterName) {
        ParameterModel removed = null;

        for (ParameterModel parameter : _parameters) {
            if (parameter.getName().equals(parameterName)) {
                removed = parameter;
                _parameters.remove(parameter);
            }
        }
        return removed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();
        for (ParameterModel pm : getParameters()) {
            String name = pm.getName();
            String value = pm.getValue();
            if (name != null && value != null) {
                map.put(name, value);
            }
        }
        return map;
    }

}
