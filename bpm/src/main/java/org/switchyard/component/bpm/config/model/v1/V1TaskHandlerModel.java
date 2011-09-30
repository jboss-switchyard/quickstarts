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

import org.switchyard.common.type.Classes;
import org.switchyard.component.bpm.config.model.TaskHandlerModel;
import org.switchyard.component.bpm.task.TaskHandler;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version TaskHandlerModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1TaskHandlerModel extends BaseModel implements TaskHandlerModel {

    /**
     * Creates a new TaskHandlerModel in the default namespace.
     */
    public V1TaskHandlerModel() {
        super(new QName(DEFAULT_NAMESPACE, TASK_HANDLER));
    }

    /**
     * Creates a new TaskHandlerModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1TaskHandlerModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends TaskHandler> getClazz() {
        String c = getModelAttribute("class");
        return c != null ? (Class<? extends TaskHandler>)Classes.forName(c, getClass()) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskHandlerModel setClazz(Class<? extends TaskHandler> clazz) {
        String c = clazz != null ? clazz.getName() : null;
        setModelAttribute("class", c);
        return this;
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
    public TaskHandlerModel setName(String name) {
        setModelAttribute("name", name);
        return this;
    }

}
