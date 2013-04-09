/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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

    /**
     * Creates new processor.
     * 
     * @param serviceName Service name.
     * @param bindingModel Camel binding model bound to service interface.
     */
    public OperationSelectorProcessor(QName serviceName, CamelBindingModel bindingModel) {
        this._serviceName = serviceName;
        this._bindingModel = bindingModel;
    }

    @Override
    public void process(org.apache.camel.Exchange exchange) throws Exception {
        OperationSelectorFactory<CamelBindingData> selectorFactory = OperationSelectorFactory.getOperationSelectorFactory(CamelBindingData.class);
        OperationSelector<CamelBindingData> selector = selectorFactory.newOperationSelector(_bindingModel.getOperationSelector());

        if (selector != null) {
            selector.setDefaultNamespace(Strings.trimToNull(_serviceName.getNamespaceURI()));
        }

        Message in = exchange.getIn();
        in.setHeader(OPERATION_SELECTOR_HEADER, selector);
        exchange.setOut(in.copy());
    }

}

