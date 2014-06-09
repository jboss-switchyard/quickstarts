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
package org.switchyard.deploy.osgi.internal.http;

import org.osgi.service.http.HttpService;
import org.switchyard.ServiceDomain;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.Endpoint;
import org.switchyard.component.http.HttpGatewayServlet;
import org.switchyard.component.http.InboundHandler;
import org.switchyard.component.http.endpoint.EndpointPublisher;

/**
 * Publishes HTTP endpoint via OSGi HttpService.
 */
public class OsgiHttpEndpointPublisher implements EndpointPublisher {

    private HttpService _httpService;

    public void setHttpService(HttpService httpService) {
        _httpService = httpService;
    }

    @Override
    public Endpoint publish(ServiceDomain domain, String context, InboundHandler handler) throws Exception {
        String alias = "/" + context;
        HttpGatewayServlet servlet = new HttpGatewayServlet();
        servlet.setHandler(handler);
        servlet.setClassLoader(Classes.getTCCL());
        _httpService.registerServlet(alias, servlet, null, null);
        return new OsgiHttpEndpoint(_httpService, alias);
    }

}
