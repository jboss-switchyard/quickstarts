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
import org.switchyard.component.jca.config.model.EndpointModel;
import org.switchyard.component.jca.config.model.InboundInteractionModel;
import org.switchyard.component.jca.config.model.ListenerModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * V1 InboundInteraction model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class V1InboundInteractionModel extends BaseModel implements InboundInteractionModel {

    /**
     * Constructor.
     */
    public V1InboundInteractionModel() {
        super(new QName(JCAConstants.DEFAULT_NAMESPACE, JCAConstants.INBOUND_INTERACTION));
        setModelChildrenOrder(JCAConstants.LISTENER, JCAConstants.ENDPOINT, JCAConstants.TRANSACTED, JCAConstants.BATCH_COMMIT);
    }
    
    /**
     * Constructor.
     * 
     * @param config configuration
     * @param desc description
     */
    public V1InboundInteractionModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public ListenerModel getListener() {
        return (ListenerModel) getFirstChildModel(JCAConstants.LISTENER);
    }

    @Override
    public InboundInteractionModel setListener(ListenerModel listener) {
        setChildModel(listener);
        return this;
    }

    @Override
    public EndpointModel getEndpoint() {
        return (EndpointModel) getFirstChildModel(JCAConstants.ENDPOINT);
    }

    @Override
    public InboundInteractionModel setEndpoint(EndpointModel endpoint) {
        setChildModel(endpoint);
        return this;
    }

    @Override
    public boolean isTransacted() {
        Configuration config = getModelConfiguration().getFirstChild(JCAConstants.TRANSACTED);
        return config != null ? Boolean.parseBoolean(config.getValue()) : false;
    }

    @Override
    public InboundInteractionModel setTransacted(boolean transacted) {
        Configuration config = getModelConfiguration().getFirstChild(JCAConstants.TRANSACTED);
        if (config != null) {
            config.setValue(Boolean.toString(transacted));
        } else {
            V1NameValueModel model = new V1NameValueModel(JCAConstants.TRANSACTED);
            model.setValue(Boolean.toString(transacted));
            setChildModel(model);
        }
        return this;
    }

    @Override
    public BatchCommitModel getBatchCommit() {
        return (BatchCommitModel) getFirstChildModel(JCAConstants.BATCH_COMMIT);
    }

    @Override
    public InboundInteractionModel setBatchCommit(BatchCommitModel batchCommit) {
        setChildModel(batchCommit);
        return this;
    }

}
