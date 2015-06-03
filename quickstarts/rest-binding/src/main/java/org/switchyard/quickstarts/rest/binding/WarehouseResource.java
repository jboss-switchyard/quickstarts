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
package org.switchyard.quickstarts.rest.binding;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;

/**
 * REST interface for WarehouseService.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
@Path("/warehouse")
public interface WarehouseResource {

    @GET
    @Path("/get/{itemId}")
    @Produces({ "text/xml" })
    public Response getItem(@PathParam("itemId") Integer itemId) throws Exception;

    @PUT
    @Path("{itemId}/{desc}")
    @Produces({ "text/plain" })
    public String addItem(@PathParam("itemId") Integer itemId, @PathParam("desc") String description) throws Exception;

    @POST
    @Path("/")
    @Consumes({ "text/xml" })
    public String updateItem(Item item) throws Exception;

    @DELETE
    @Path("{itemId}")
    @Produces({ "text/plain" })
    public String removeItem(@PathParam("itemId") Integer itemId) throws Exception;

    @GET
    @Path("/count/")
    @Produces({ "text/plain" })
    public Integer getItemCount();
}
