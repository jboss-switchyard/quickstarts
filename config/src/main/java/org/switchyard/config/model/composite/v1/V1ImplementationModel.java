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
package org.switchyard.config.model.composite.v1;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.BaseTypedModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.ComponentImplementationModel;

/**
 * V1ImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1ImplementationModel extends BaseTypedModel implements ComponentImplementationModel {

    public V1ImplementationModel(String type) {
        super(new QName(CompositeModel.DEFAULT_NAMESPACE, ComponentImplementationModel.IMPLEMENTATION + '.' + type));
    }

    public V1ImplementationModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public ComponentModel getComponent() {
        return (ComponentModel)getParentModel();
    }

    @Override
    public String getClazz() {
        return getModelAttribute(ComponentImplementationModel.CLASS);
    }

    @Override
    public ComponentImplementationModel setClazz(String clazz) {
        setModelAttribute(ComponentImplementationModel.CLASS, clazz);
        return this;
    }

}
