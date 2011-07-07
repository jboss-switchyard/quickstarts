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

import static org.switchyard.component.bpm.config.model.BpmComponentImplementationModel.DEFAULT_NAMESPACE;

import javax.xml.namespace.QName;

import org.switchyard.component.bpm.config.model.ResourceModel;
import org.switchyard.component.bpm.resource.ResourceType;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version ResourceModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1ResourceModel extends BaseModel implements ResourceModel {

    /**
     * Creates a new ResourceModel in the default namespace.
     */
    public V1ResourceModel() {
        super(new QName(DEFAULT_NAMESPACE, RESOURCE));
    }

    /**
     * Creates a new ResourceModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1ResourceModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocation() {
        return getModelAttribute("location");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceModel setLocation(String location) {
        setModelAttribute("location", location);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceType getType() {
        String prt = getModelAttribute("type");
        return prt != null ? ResourceType.valueOf(prt) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceModel setType(ResourceType type) {
        String prt = type != null ? type.name() : null;
        setModelAttribute("type", prt);
        return this;
    }

}
