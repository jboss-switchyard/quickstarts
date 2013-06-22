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

import org.switchyard.component.common.knowledge.OperationType;
import org.switchyard.component.common.knowledge.config.model.v1.V1OperationModel;
import org.switchyard.component.bpm.BPMOperationType;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version BPMOperationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1BPMOperationModel extends V1OperationModel {

    /**
     * Creates a new V1BPMOperationModel.
     */
    public V1BPMOperationModel() {
        super(DEFAULT_NAMESPACE);
    }

    /**
     * Creates a new V1BPMOperationModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1BPMOperationModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OperationType getType() {
        String type = getModelAttribute("type");
        return type != null ? BPMOperationType.valueOf(type) : null;
    }

}
