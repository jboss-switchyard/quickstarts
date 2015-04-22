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
 
package org.switchyard.as7.extension.resteasy;

import java.util.ArrayList;
import java.util.List;

import org.switchyard.as7.extension.WebResource;
import org.switchyard.component.common.Endpoint;

/**
 * A standalone RESTEasy resource.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class RESTEasyResource implements Endpoint {

    private List<Object> _instances = new ArrayList<Object>();
    private WebResource _resource;

    /**
     * Gets the list of instances associated with this resource deployment.
     * @return The List of instances
     */
    public List<Object> getInstances() {
        return _instances;
    }

    /**
     * Sets the list of instances associated with this resource deployment.
     * @param instances The List of instances
     */
    public void setInstances(List<Object> instances) {
        _instances = instances;
    }

    /**
     * Gets the WebResource associated with this resource deployment.
     * @return The WebResource
     */
    public WebResource getWebResource() {
        return _resource;
    }

    /**
     * Sets the WebResource associated with this resource deployment.
     * @param resource The resource
     */
    public void setWebResource(WebResource resource) {
        _resource = resource;
    }

    @Override
    public void start() {
        _resource.start(this);
        ((RESTEasyServlet)_resource.getDeployment().getServlets().get(0).getServlet()).addInstances(getInstances());
    }

    @Override
    public void stop() {
        ((RESTEasyServlet)_resource.getDeployment().getServlets().get(0).getServlet()).removeInstances(getInstances());
        _resource.stop(this);
    }
}
