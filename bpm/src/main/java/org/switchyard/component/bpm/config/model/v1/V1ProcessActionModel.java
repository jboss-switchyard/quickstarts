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
package org.switchyard.component.bpm.config.model.v1;

import static org.switchyard.component.bpm.config.model.BPMComponentImplementationModel.DEFAULT_NAMESPACE;

import javax.xml.namespace.QName;

import org.switchyard.component.bpm.common.ProcessActionType;
import org.switchyard.component.bpm.config.model.ProcessActionModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version ProcessActionModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1ProcessActionModel extends BaseModel implements ProcessActionModel {

    /**
     * Creates a new ProcessActionModel in the default namespace.
     */
    public V1ProcessActionModel() {
        super(new QName(DEFAULT_NAMESPACE, ACTION));
    }

    /**
     * Creates a new ProcessActionModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1ProcessActionModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getModelAttribute("name");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessActionModel setName(String name) {
        setModelAttribute("name", name);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessActionType getType() {
        String pat = getModelAttribute("type");
        return pat != null ? ProcessActionType.valueOf(pat) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessActionModel setType(ProcessActionType type) {
        String pat = type != null ? type.name() : null;
        setModelAttribute("type", pat);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventType() {
        return getModelAttribute("eventType");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessActionModel setEventType(String eventType) {
        setModelAttribute("eventType", eventType);
        return this;
    }

}
