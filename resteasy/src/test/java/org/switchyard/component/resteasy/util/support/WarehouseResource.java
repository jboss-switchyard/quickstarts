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

package org.switchyard.component.resteasy.util.support;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;

/**
 * REST interface for WarehouseService.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
@Path("/warehouse")
public interface WarehouseResource {

    @GET
    @Path("{itemId}")
    @Produces({"application/xml"})
    public Item getItem(@PathParam("itemId") Integer itemId);

    @PUT
    @Path("/")
    @Consumes({"application/xml"})
    public String addItem(Item item) throws Exception;

    @POST
    @Path("/")
    @Consumes({"application/xml"})
    public String updateItem(Item item) throws Exception;

    @DELETE
    @Path("{itemId}")
    public String removeItem(@PathParam("itemId") Integer itemId) throws Exception;

    @GET
    @Path("/count/")
    public Integer getItemCount();

    public void testVoid();
}
