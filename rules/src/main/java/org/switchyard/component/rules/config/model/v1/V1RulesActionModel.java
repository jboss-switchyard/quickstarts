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
package org.switchyard.component.rules.config.model.v1;

import static org.switchyard.component.rules.config.model.RulesComponentImplementationModel.DEFAULT_NAMESPACE;

import javax.xml.namespace.QName;

import org.switchyard.component.rules.common.RulesActionType;
import org.switchyard.component.rules.config.model.RulesActionModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version RulesActionModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1RulesActionModel extends BaseModel implements RulesActionModel {

    /**
     * Creates a new RulesActionModel in the default namespace.
     */
    public V1RulesActionModel() {
        super(new QName(DEFAULT_NAMESPACE, ACTION));
    }

    /**
     * Creates a new RulesActionModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1RulesActionModel(Configuration config, Descriptor desc) {
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
    public RulesActionModel setName(String name) {
        setModelAttribute("name", name);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RulesActionType getType() {
        String rat = getModelAttribute("type");
        return rat != null ? RulesActionType.valueOf(rat) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RulesActionModel setType(RulesActionType type) {
        String rat = type != null ? type.name() : null;
        setModelAttribute("type", rat);
        return this;
    }

}
