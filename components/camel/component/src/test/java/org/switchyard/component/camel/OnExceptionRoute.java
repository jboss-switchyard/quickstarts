/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.camel;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.switchyard.component.camel.switchyard.ExchangeMapper;

public class OnExceptionRoute extends RouteBuilder {

	/**
	 * The Camel route is configured via this method.  The from endpoint is required to be a SwitchYard service.
	 */
	public void configure() {
		from("switchyard://OnExceptionService")
			.onException(Exception.class)
				.handled(true)
				.setBody().simple("handled:${exception.message}")
				.end()
			.process(new Processor() {
			    public void process(Exchange exchange) throws Exception {
			        // List of propagated Camel properties
	                List<String> includeProps = new ArrayList<String>();
	                includeProps.add("Camel-abc");
	                includeProps.add("Camel-xyz");
	                exchange.setProperty(ExchangeMapper.CAMEL_PROPERTY_INCLUDES, includeProps);
	                
	                // Set two properties, one of which should be propagated
	                exchange.setProperty("Camel-abc", "Camel-abc");
                    exchange.setProperty("Camel-123", "Camel-123");
			    }
			})
			.to("switchyard://Service1")
			.setBody().simple("happy path");
	}

}
