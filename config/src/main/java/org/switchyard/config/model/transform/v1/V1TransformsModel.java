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
package org.switchyard.config.model.transform.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.TransformsModel;

/**
 * A version 1 TransformsModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1TransformsModel extends BaseModel implements TransformsModel {

    private List<TransformModel> _transforms = new ArrayList<TransformModel>();

    /**
     * Constructs a new V1TransformsModel.
     */
    public V1TransformsModel() {
        super(new QName(SwitchYardModel.DEFAULT_NAMESPACE, TransformsModel.TRANSFORMS));
        setModelChildrenOrder(TransformModel.TRANSFORM);
    }

    /**
     * Constructs a new V1TransformsModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1TransformsModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration transform_config : config.getChildrenStartsWith(TransformModel.TRANSFORM)) {
            TransformModel transform = (TransformModel)readModel(transform_config);
            if (transform != null) {
                _transforms.add(transform);
            }
        }
        setModelChildrenOrder(TransformModel.TRANSFORM);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SwitchYardModel getSwitchYard() {
        return (SwitchYardModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<TransformModel> getTransforms() {
        return Collections.unmodifiableList(_transforms);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized TransformsModel addTransform(TransformModel transform) {
        addChildModel(transform);
        _transforms.add(transform);
        return this;
    }

}
