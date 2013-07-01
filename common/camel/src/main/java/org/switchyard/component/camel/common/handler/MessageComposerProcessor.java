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
package org.switchyard.component.camel.common.handler;

import static org.switchyard.component.camel.common.CamelConstants.MESSAGE_COMPOSER_HEADER;

import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.camel.common.composer.CamelComposition;
import org.switchyard.component.camel.common.model.CamelBindingModel;
import org.switchyard.component.common.composer.MessageComposer;

/**
 * Processor used to choose operation from binding model.
 */
public class MessageComposerProcessor implements Processor {

    private final MessageComposer<CamelBindingData> _composer;

    /**
     * Creates new processor.
     * 
     * @param bindingModel Camel binding model bound to service interface.
     */
    public MessageComposerProcessor(CamelBindingModel bindingModel) {
        _composer = CamelComposition.getMessageComposer(bindingModel);
    }

    @Override
    public void process(org.apache.camel.Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        in.setHeader(MESSAGE_COMPOSER_HEADER, _composer);
        exchange.setOut(in.copy());
    }

}

