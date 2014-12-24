/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
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
package org.switchyard.quickstarts.rest.binding;

import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ClientInterceptor;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.spi.interception.ClientExecutionContext;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;

@Provider
@ClientInterceptor
public class MyClientExecutionInterceptor implements ClientExecutionInterceptor {

    public ClientResponse execute(ClientExecutionContext ctx) throws Exception {
        ClientRequest request = ctx.getRequest();
        ClientResponse response = null;

        response = ctx.proceed();
        if ((response.getResponseStatus() != null) && (response.getResponseStatus().getStatusCode() == 404)) {
            BaseClientResponse r = (BaseClientResponse) response;
            MultivaluedMap<String, String> headers = r.getHeaders();
            headers.add("full-path", request.getUri());
            r.setHeaders(headers);
        }
        return response;
    }
}
