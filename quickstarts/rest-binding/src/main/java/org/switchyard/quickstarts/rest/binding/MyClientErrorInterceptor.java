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

import java.io.InputStream;
import java.io.IOException;

import javax.ws.rs.core.Response;
import javax.ws.rs.WebApplicationException;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.ClientErrorInterceptor;

public class MyClientErrorInterceptor implements ClientErrorInterceptor {

    public void handle(ClientResponse response) throws RuntimeException {
        try {
            BaseClientResponse r = (BaseClientResponse) response;

            InputStream stream = r.getStreamFactory().getInputStream();
            if (stream != null) {
                stream.reset();
            }
            if ((response.getResponseStatus() != null) && (response.getResponseStatus().getStatusCode() == 404)
                && !(r.getException() instanceof ItemNotFoundException)) {
                throw new WebApplicationException(Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiError("Error at " + response.getHeaders().get("full-path")))
                .build());
            }

        } catch (IOException e){
            //...
        }
    }
}
