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

import static org.switchyard.component.camel.common.CamelConstants.OPERATION_SELECTOR_HEADER;

import javax.xml.namespace.QName;

import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.switchyard.common.lang.Strings;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.camel.common.model.CamelBindingModel;
import org.switchyard.component.common.selector.OperationSelectorFactory;
import org.switchyard.selector.OperationSelector;

/**
 * Processor used to choose operation from binding model.
 */
public class OperationSelectorProcessor implements Processor {

    private final QName _serviceName;
    private final CamelBindingModel _bindingModel;    
    private final OperationSelector<CamelBindingData> _selector;

    /**
     * Creates new processor.
     * 
     * @param serviceName Service name.
     * @param bindingModel Camel binding model bound to service interface.
     */
    public OperationSelectorProcessor(QName serviceName, CamelBindingModel bindingModel) {
        this._serviceName = serviceName;
        this._bindingModel = bindingModel;
        
        OperationSelectorFactory<CamelBindingData> selectorFactory = OperationSelectorFactory.getOperationSelectorFactory(CamelBindingData.class);
        _selector = selectorFactory.newOperationSelector(_bindingModel.getOperationSelector());
        if (_selector != null) {
            _selector.setDefaultNamespace(Strings.trimToNull(_serviceName.getNamespaceURI()));
        }
    }

    @Override
    public void process(org.apache.camel.Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        in.setHeader(OPERATION_SELECTOR_HEADER, _selector);
        exchange.setOut(in.copy());
    }

}

