/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.soap.attachment;

import javax.activation.DataHandler;
import javax.inject.Named;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

@Named("customProcessor")
public class CustomProcessor implements Processor {

    /**
     * Creates new processor.
     */
    public CustomProcessor() {
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String newFileName = "external-switchyard.png";
        DataHandler image = exchange.getIn().getAttachment(exchange.getIn().getBody(String.class));
        exchange.getOut().addAttachment(newFileName, image);
        exchange.getOut().setBody(newFileName);
    }

}
