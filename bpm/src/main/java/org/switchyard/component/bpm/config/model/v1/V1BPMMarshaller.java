/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.bpm.config.model.v1;

import static org.switchyard.component.bpm.config.model.ProcessActionModel.ACTION;
import static org.switchyard.component.bpm.config.model.TaskHandlerModel.TASK_HANDLER;
import static org.switchyard.component.common.rules.config.model.AuditModel.AUDIT;
import static org.switchyard.config.model.resource.ResourceModel.RESOURCE;

import org.switchyard.component.bpm.config.model.BPMComponentImplementationModel;
import org.switchyard.component.common.rules.config.model.v1.V1AuditModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.v1.V1CompositeMarshaller;
import org.switchyard.config.model.resource.v1.V1ResourceModel;

/**
 * A CompositeMarshaller which can also create BPMComponentImplementationModels, ResourceModels and TaskHandlerModels.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1BPMMarshaller extends V1CompositeMarshaller {

    /**
     * The complete local name ("implementation.bpm").
     */
    private static final String IMPLEMENTATION_BPM = ComponentImplementationModel.IMPLEMENTATION + "." + BPMComponentImplementationModel.BPM;

    /**
     * Required constructor called via reflection.
     *
     * @param desc the Descriptor
     */
    public V1BPMMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * Reads in the Configuration, looking for "implementation.bpm", "resource" or "taskHandler".
     * If not found, it falls back to the super class (V1CompositeMarshaller).
     *
     * @param config the Configuration
     * @return the Model
     */
    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        if (IMPLEMENTATION_BPM.equals(name)) {
            return new V1BPMComponentImplementationModel(config, getDescriptor());
        } else if (ACTION.equals(name)) {
            return new V1ProcessActionModel(config, getDescriptor());
        } else if (AUDIT.equals(name)) {
            return new V1AuditModel(config, getDescriptor());
        } else if (TASK_HANDLER.equals(name)) {
            return new V1TaskHandlerModel(config, getDescriptor());
        } else if (RESOURCE.equals(name)) {
            return new V1ResourceModel(config, getDescriptor());
        }
        return super.read(config);
    }

}
