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

package org.switchyard.quickstarts.rest.binding;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;

/**
 * REST interface for OrderService.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
@Path("/order")
public interface OrderResource {

    @POST
    @Path("/")
    @Produces({"text/xml"})
    public Order newOrder();

    @GET
    @Path("{orderId}")
    @Produces({"text/xml"})
    public Order getOrder(@PathParam("orderId") Integer orderId);

    @PUT
    @Path("/item")
    @Consumes({"text/xml"})
    public String addItems(Order order);

    @DELETE
    @Path("{itemId}")
    public String removeItem(@PathParam("itemId") String orderItemId);

}
