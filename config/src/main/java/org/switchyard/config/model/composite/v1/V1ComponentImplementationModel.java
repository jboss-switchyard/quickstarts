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
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.CompositeModel;

/**
 * A version 1 ComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1ComponentImplementationModel extends BaseTypedModel implements ComponentImplementationModel {

    /**
     * Constructs a new V1ComponentImplementationModel of the specified "type".
     * @param type the "type" of ComponentImplementationModel
     */
    public V1ComponentImplementationModel(String type) {
        this(type, CompositeModel.DEFAULT_NAMESPACE);
    }

    /**
     * Constructs a new V1ComponentImplementationModel of the specified "type", and in the specified namespace.
     * @param type the "type" of ComponentImplementationModel
     * @param namespace the namespace
     */
    public V1ComponentImplementationModel(String type, String namespace) {
        super(new QName(namespace, ComponentImplementationModel.IMPLEMENTATION + '.' + type));
    }

    /**
     * Constructs a new V1ComponentImplementationModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1ComponentImplementationModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComponentModel getComponent() {
        return (ComponentModel)getModelParent();
    }

}
