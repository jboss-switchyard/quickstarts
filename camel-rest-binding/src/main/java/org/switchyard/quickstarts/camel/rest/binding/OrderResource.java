/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.quickstarts.camel.rest.binding;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

/**
 * REST interface for OrderService.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
// RESTEasy/JBoss JAXRS spec is more sensitive with extra path slashes
// @Path("/")
public class OrderResource {

    @POST
    @Path("/")
    public Order newOrder() {
        return null;
    }

    @GET
    // RESTEasy/JBoss JAXRS spec is more sensitive with extra path slashes
    // @Path("/{itemId}")
    @Path("{orderId}")
    public Order getOrder(@PathParam("orderId") Integer orderId) {
        return null;
    }

    @PUT
    @Path("/item")
    public String addItems(Order order) {
        return null;
    }

    @DELETE
    // RESTEasy/JBoss JAXRS spec is more sensitive with extra path slashes
    // @Path("/{itemId}")
    @Path("{itemId}")
    public String removeItem(@PathParam("itemId") String orderItemId) {
        return null;
    }

}
