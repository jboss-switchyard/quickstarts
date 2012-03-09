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

package org.switchyard.config.model.composite.v1;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseTypedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.InterfaceModel;

/**
 * A representation of an InterfaceModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1InterfaceModel extends BaseTypedModel implements InterfaceModel {
    
    /**
     * Creates a new, empty interface model of the specified type.
     * @param type interface type
     */
    public V1InterfaceModel(String type) {
        this(type, CompositeModel.DEFAULT_NAMESPACE);
    }

    /**
     * Creates a new, empty interface model with the specified type and namespace.
     * @param type interface type
     * @param namespace namespace
     */
    public V1InterfaceModel(String type, String namespace) {
        super(new QName(namespace, InterfaceModel.INTERFACE + '.' + type));
    }

    /**
     * Creates a new InterfaceModel from the specified configuration.
     * @param config configuration
     * @param desc descriptor
     */
    public V1InterfaceModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInterface() {
        return getModelAttribute(InterfaceModel.INTERFACE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InterfaceModel setInterface(String interfaze) {
        setModelAttribute(InterfaceModel.INTERFACE, interfaze);
        return this;
    }

}
