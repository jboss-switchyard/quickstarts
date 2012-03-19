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

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;

/**
 * RPC like REST interface for Test methods.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
// RESTEasy/JBoss JAXRS spec is more sensitive with extra path slashes
// @Path("/inventory")
@Path("inventory")
public class TestResource {

    /**
     * Convenience method for test cases.
     */
    @OPTIONS
    @Path("/create/")
    public String createInventory() {
        return null;
    }

    /**
     * Convenience method for test cases.
     */
    @OPTIONS
    @Path("/update/")
    public String updateInventory() {
        return null;
    }

    /**
     * Convenience method for test cases.
     */
    @OPTIONS
    @Path("/remove/")
    public String removeInventory() {
        return null;
    }

    /**
     * Convenience method for test cases.
     */
    @GET
    @Path("/")
    public Boolean isInventorySetup() {
        return null;
    }

}
