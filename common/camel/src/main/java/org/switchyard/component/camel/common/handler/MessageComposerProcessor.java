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

