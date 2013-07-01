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
package org.switchyard.component.jca.config.model.v1;

import javax.xml.namespace.QName;

import org.switchyard.component.jca.JCAConstants;
import org.switchyard.component.jca.config.model.BatchCommitModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * V1 BatchCommitModel.
 */
public class V1BatchCommitModel extends BaseModel implements BatchCommitModel {

    /**
     * Constructor.
     */
    public V1BatchCommitModel() {
        super(new QName(JCAConstants.DEFAULT_NAMESPACE, JCAConstants.BATCH_COMMIT));
    }
    
    /**
     * Constructor.
     * @param config configuration
     * @param desc description
     */
    public V1BatchCommitModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }
    
    @Override
    public long getBatchTimeout() {
        return Long.parseLong(getModelAttribute(JCAConstants.BATCH_TIMEOUT));
    }

    @Override
    public BatchCommitModel setBatchTimeout(long delay) {
        setModelAttribute(JCAConstants.BATCH_TIMEOUT, Long.toString(delay));
        return this;
    }

    @Override
    public int getBatchSize() {
        return Integer.parseInt(getModelAttribute(JCAConstants.BATCH_SIZE));
    }

    @Override
    public BatchCommitModel setBatchSize(int size) {
        setModelAttribute(JCAConstants.BATCH_SIZE, Integer.toString(size));
        return this;
    }

}
