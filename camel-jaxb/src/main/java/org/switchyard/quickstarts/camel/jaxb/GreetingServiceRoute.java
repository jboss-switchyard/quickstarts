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
package org.switchyard.quickstarts.camel.jaxb;

import javax.xml.bind.JAXBContext;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.switchyard.common.camel.SwitchYardMessage;

/**
 * Greeting service implementation.
 */
public class GreetingServiceRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        JaxbDataFormat jxb = new JaxbDataFormat(JAXBContext.newInstance("org.switchyard.quickstarts.camel.jaxb"));
        from("switchyard://GreetingService")
            .convertBodyTo(String.class)
            .unmarshal(jxb)
            .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    GreetingRequest rq = exchange.getIn().getBody(GreetingRequest.class);
                    GreetingResponse rp = new GreetingResponse("Ola " + rq.getName() + "!");

                    SwitchYardMessage out = new SwitchYardMessage();
                    out.setBody(rp);
                    exchange.setOut(out);
                }
            })
            .marshal(jxb)
            .convertBodyTo(String.class);
    }

}
