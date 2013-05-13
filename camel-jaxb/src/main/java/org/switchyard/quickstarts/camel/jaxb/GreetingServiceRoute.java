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
