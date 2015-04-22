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
package org.switchyard.quickstarts.camel.hl7example;

import ca.uhn.hl7v2.model.v24.message.QRY_A19;
import ca.uhn.hl7v2.model.v24.segment.QRD;
import ca.uhn.hl7v2.parser.PipeParser;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hl7.HL7DataFormat;
import org.apache.camel.spi.DataFormat;
import org.switchyard.common.camel.SwitchYardMessage;

public class CamelServiceRoute extends RouteBuilder {

    /**
     * The Camel route is configured via this method.  The from endpoint is required to be a SwitchYard service.
     */
    public void configure() {
        DataFormat hl7 = new HL7DataFormat();        
        from("switchyard://HL7Route")
            .unmarshal(hl7)
             .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    String body = exchange.getIn().getBody(String.class);
                    
                    PipeParser pipeParser = new PipeParser();                   
                    try {
                        // Parse the HL7 message 
                        ca.uhn.hl7v2.model.Message message = pipeParser.parse(body);
                        if (message instanceof QRY_A19) {                         
                            // Print out some details from the QRD
                            QRD qrd = (QRD) message.get("QRD");
                            System.out.println("Query Date/Time : " + qrd.getQueryDateTime().getTimeOfAnEvent().getValue());
                            System.out.println("Query Format Code : " + qrd.getQueryFormatCode().getValue());
                            System.out.println("Query Priority : " + qrd.getQueryPriority().getValue());
                            System.out.println("Query ID : " + qrd. getQueryID().getValue());
                            System.out.println("Deferred Response Type : " + qrd.getDeferredResponseType().getValue());
                            System.out.println("Deferred Response Date/Time : " + qrd.getDeferredResponseDateTime().getTimeOfAnEvent().getValue());
                            System.out.println("Quantity Limited Request : " + qrd.getQuantityLimitedRequest().getQuantity().getValue());
                            System.out.println("Query Results Level : " + qrd.getQueryResultsLevel().getValue());
                            qrd.getQueryID();
                        } 
                    } catch (Exception e) {
                        throw e;
                    }
         
                    SwitchYardMessage out = new SwitchYardMessage();
                    out.setBody(body);
                    exchange.setOut(out);
                }
            });
    }

}
