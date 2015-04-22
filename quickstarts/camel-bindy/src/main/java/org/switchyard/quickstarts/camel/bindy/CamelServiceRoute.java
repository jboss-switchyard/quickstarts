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
package org.switchyard.quickstarts.camel.bindy;

import org.switchyard.quickstarts.camel.bindy.Order;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;

public class CamelServiceRoute extends RouteBuilder {

    
    DataFormat bindy = new BindyCsvDataFormat("org.switchyard.quickstarts.camel.bindy");
    
    /**
     * The Camel route is configured via this method.  The from endpoint is required to be a SwitchYard service.
     */
    public void configure() {
        // TODO Auto-generated method stub
        from("switchyard://BindyRoute")
            .log("Received message for 'BindyRoute' : ${body}")
            .unmarshal(bindy)
            .process(new Processor() {
                public void process(Exchange exchange) throws Exception {
                    Message in = exchange.getIn();
                    @SuppressWarnings("unchecked")
                    ArrayList<HashMap<String, Order>> list = (ArrayList<HashMap<String, Order>>) in.getBody();
                    for (HashMap<String, Order> map : list) {
                        for (Order order : map.values()) {
                            if (order.getProduct().equals("Lucky Charms")) {
                                order.setPrice("17");
                            }
                            if (order.getProduct().equals("Grape Nuts")) {
                                order.setProduct("Cheerios");
                            }                                  
                        }
                    }                    
                }
            })
            .marshal(bindy)
            .to("file:///tmp/output.txt")
            .end();
           
    }

}
