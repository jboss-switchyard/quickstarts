/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.component.common.knowledge.config.model.v1;

import static org.switchyard.component.common.knowledge.config.model.FaultModel.FAULT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.config.model.FaultModel;
import org.switchyard.component.common.knowledge.config.model.FaultsModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 1 FaultsModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1FaultsModel extends BaseModel implements FaultsModel {

    private List<FaultModel> _faults = new ArrayList<FaultModel>();

    /**
     * Creates a new V1FaultsModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1FaultsModel(String namespace) {
        super(XMLHelper.createQName(namespace, FAULTS));
        setModelChildrenOrder(FAULT);
    }

    /**
     * Creates a new V1FaultsModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1FaultsModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration fault_config : config.getChildren(FAULT)) {
            FaultModel fault = (FaultModel)readModel(fault_config);
            if (fault != null) {
                _faults.add(fault);
            }
        }
        setModelChildrenOrder(FAULT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<FaultModel> getFaults() {
        return Collections.unmodifiableList(_faults);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FaultsModel addFault(FaultModel fault) {
        addChildModel(fault);
        _faults.add(fault);
        return this;
    }

}
