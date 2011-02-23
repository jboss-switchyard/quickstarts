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
package org.switchyard.config.model.switchyard.test.smooks;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * SmooksConfigModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class SmooksConfigModel extends BaseModel {

    public static final String CONFIG = "config";

    public SmooksConfigModel() {
        super(new QName(SmooksTransformModel.DEFAULT_NAMESPACE, CONFIG));
    }

    public SmooksConfigModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    public SmooksTransformModel getTransform() {
        return (SmooksTransformModel)getModelParent();
    }

    public String getData() {
        return getModelValue();
    }

    public SmooksConfigModel setData(String data) {
        setModelValue(data);
        return this;
    }

}
