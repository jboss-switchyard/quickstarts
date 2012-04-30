/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.bpm.config.model.v1;

import static org.switchyard.component.bpm.config.model.BPMComponentImplementationModel.DEFAULT_NAMESPACE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.component.bpm.config.model.ParametersModel;
import org.switchyard.component.common.rules.config.model.MappingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version ParametersModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public class V1ParametersModel extends BaseModel implements ParametersModel {

    private List<MappingModel> _mappings = new ArrayList<MappingModel>();

    /**
     * Creates a new V1ParametersModel in the default namespace.
     */
    public V1ParametersModel() {
        super(new QName(DEFAULT_NAMESPACE, PARAMETERS));
        setModelChildrenOrder(MappingModel.MAPPING);
    }

    /**
     * Constructs a new V1ParametersModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1ParametersModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration mapping_config : config.getChildren(MappingModel.MAPPING)) {
            MappingModel mapping = (MappingModel)readModel(mapping_config);
            if (mapping != null) {
                _mappings.add(mapping);
            }
        }
        setModelChildrenOrder(MappingModel.MAPPING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MappingModel> getMappings() {
        return Collections.unmodifiableList(_mappings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ParametersModel addMapping(MappingModel mapping) {
        addChildModel(mapping);
        _mappings.add(mapping);
        return this;
    }

}
