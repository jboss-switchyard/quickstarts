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

import org.switchyard.component.jca.JCAConstants;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseMarshaller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;
import org.switchyard.config.model.composer.v1.V1ContextMapperModel;
import org.switchyard.config.model.composer.v1.V1MessageComposerModel;

/**
 * V1 model marshaller for jca binding.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class V1JCAModelMarshaller extends BaseMarshaller {

    /**
     * Constructor.
     * 
     * @param desc The descriptor for this model.
     */
    public V1JCAModelMarshaller(Descriptor desc) {
        super(desc);
    }

    @Override
    public Model read(final Configuration config) {
        final String name = config.getName();
        
        if (name.equals(JCAConstants.BINDING + "." + JCAConstants.JCA)) {
           return new V1JCABindingModel(config, getDescriptor());
        }
        if (name.equals(ContextMapperModel.CONTEXT_MAPPER)) {
            return new V1ContextMapperModel(config, getDescriptor());
        }
        if (name.equals(MessageComposerModel.MESSAGE_COMPOSER)) {
            return new V1MessageComposerModel(config, getDescriptor());
        }
        if (name.equals(JCAConstants.OUTBOUND_CONNECTION)) {
            return new V1OutboundConnectionModel(config, getDescriptor());
        }
        if (name.equals(JCAConstants.INBOUND_CONNECTION)) {
            return new V1InboundConnectionModel(config, getDescriptor());
        }
        if (name.equals(JCAConstants.OUTBOUND_INTERACTION)) {
            return new V1OutboundInteractionModel(config, getDescriptor());
        }
        if (name.equals(JCAConstants.INBOUND_INTERACTION)) {
            return new V1InboundInteractionModel(config, getDescriptor());
        }
        
        if (name.equals(JCAConstants.RESOURCE_ADAPTER)) {
            return new V1ResourceAdapterModel(config, getDescriptor());
        }
        if (name.equals(JCAConstants.CONNECTION)) {
            return new V1ConnectionModel(config, getDescriptor());
        }
        if (name.equals(JCAConstants.ACTIVATION_SPEC)) {
            return new V1ActivationSpecModel(config, getDescriptor());
        }
        if (name.equals(JCAConstants.CONNECTION_SPEC)) {
            return new V1ConnectionSpecModel(config, getDescriptor());
        }
        if (name.equals(JCAConstants.INTERACTION_SPEC)) {
            return new V1InteractionSpecModel(config, getDescriptor());
        }
        if (name.equals(JCAConstants.OPERATION)) {
            return new V1OperationModel(config, getDescriptor());
        }
        if (name.equals(JCAConstants.LISTENER)) {
            return new V1ListenerModel(config, getDescriptor());
        }
        if (name.equals(JCAConstants.ENDPOINT)) {
            return new V1EndpointModel(config, getDescriptor());
        }
        if (name.equals(JCAConstants.PROCESSOR)) {
            return new V1ProcessorModel(config, getDescriptor());
        }
        if (name.equals(JCAConstants.BATCH_COMMIT)) {
            return new V1BatchCommitModel(config, getDescriptor());
        }
        return null;
    }

}
