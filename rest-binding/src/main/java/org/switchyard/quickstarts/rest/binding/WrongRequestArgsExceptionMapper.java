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

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author Vaclav Chalupa <vchalupa@redhat.com> (C) 2014 Red Hat Inc.
 */
@Provider
public class WrongRequestArgsExceptionMapper implements ExceptionMapper<WrongRequestArgsException> {

    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(WrongRequestArgsException ex) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new ApiError(ex.getMessage()))
                .type(headers.getMediaType() != null ? headers.getMediaType() : MediaType.TEXT_XML_TYPE)
                .build();
    }
}
