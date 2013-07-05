/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    public static final String DEFAULT_NAMESPACE = "urn:switchyard-config:test-smooks:1.0";
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
