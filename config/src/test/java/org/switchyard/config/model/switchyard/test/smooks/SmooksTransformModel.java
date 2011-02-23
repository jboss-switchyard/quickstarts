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
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.transform.v1.V1BaseTransformModel;

/**
 * SmooksTransformModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class SmooksTransformModel extends V1BaseTransformModel {

    public static final String DEFAULT_NAMESPACE = "http://www.switchyard.org/config/model/switchyard/test/smooks";
    public static final String SMOOKS = "smooks";

    private SmooksConfigModel _config;

    public SmooksTransformModel() {
        super(new QName(DEFAULT_NAMESPACE, SMOOKS));
        setModelChildrenOrder(SmooksConfigModel.CONFIG);
    }

    public SmooksTransformModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(SmooksConfigModel.CONFIG);
    }

    public SmooksConfigModel getConfig() {
        if (_config == null) {
            _config = (SmooksConfigModel)getFirstChildModelStartsWith(SmooksConfigModel.CONFIG);
        }
        return _config;
    }

    public SmooksTransformModel setConfig(SmooksConfigModel config) {
        setChildModel(config);
        _config = config;
        return this;
    }

}
