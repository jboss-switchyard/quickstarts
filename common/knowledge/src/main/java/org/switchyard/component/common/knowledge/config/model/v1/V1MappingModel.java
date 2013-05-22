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
package org.switchyard.component.common.knowledge.config.model.v1;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.config.model.MappingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version MappingModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class V1MappingModel extends BaseModel implements MappingModel {

    /**
     * Creates a new V1MappingModel in the specified namespace and localName.
     * @param namespace the specified namespace
     * @param localName the specified localName
     */
    public V1MappingModel(String namespace, String localName) {
        super(XMLHelper.createQName(namespace, localName));
    }

    /**
     * Creates a new V1MappingModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1MappingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFrom() {
        return getModelAttribute("from");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MappingModel setFrom(String from) {
        setModelAttribute("from", from);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTo() {
        return getModelAttribute("to");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MappingModel setTo(String to) {
        setModelAttribute("to", to);
        return this;
    }

}
