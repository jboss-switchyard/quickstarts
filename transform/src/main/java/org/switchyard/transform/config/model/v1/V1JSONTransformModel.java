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

package org.switchyard.transform.config.model.v1;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.v1.V1BaseTransformModel;
import org.switchyard.transform.config.model.JSONTransformModel;
import org.switchyard.transform.internal.TransformerFactoryClass;
import org.switchyard.transform.json.internal.JSONTransformFactory;

/**
 * Version 1 JSON Transform Model.
 *
 * @author Alejandro Montenegro &lt;<a href="mailto:aamonten@gmail.com">aamonten@gmail.com</a>&gt;
 */
@TransformerFactoryClass(JSONTransformFactory.class)
public class V1JSONTransformModel extends V1BaseTransformModel implements JSONTransformModel {

    /**
     * Constructor.
     */
    public V1JSONTransformModel() {
        super(new QName(TransformModel.DEFAULT_NAMESPACE, TransformModel.TRANSFORM + '.' + JSON));
    }

    /**
     * Constructor.
     * @param config configuration.
     * @param desc descriptor.
     */
    public V1JSONTransformModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }
}
