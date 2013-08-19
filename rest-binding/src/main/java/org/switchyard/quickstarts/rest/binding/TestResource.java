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

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;

/**
 * RPC like REST interface for Test methods.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
@Path("/inventory")
public interface TestResource {

    /**
     * Convenience method for test cases.
     */
    @OPTIONS
    @Path("/create/")
    public String createInventory();

    /**
     * Convenience method for test cases.
     */
    @OPTIONS
    @Path("/update/")
    public String updateInventory();

    /**
     * Convenience method for test cases.
     */
    @OPTIONS
    @Path("/remove/")
    public String removeInventory();

    /**
     * Convenience method for test cases.
     */
    @GET
    @Path("/")
    public Boolean isInventorySetup();

}
