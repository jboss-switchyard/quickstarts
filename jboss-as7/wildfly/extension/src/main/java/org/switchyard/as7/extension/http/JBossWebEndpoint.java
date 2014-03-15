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
 
package org.switchyard.as7.extension.http;

import org.switchyard.as7.extension.WebResource;
import org.switchyard.component.common.Endpoint;

/**
 * A JBossWeb HTTP endpoint.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class JBossWebEndpoint implements Endpoint {

    private WebResource _resource;

    /**
     * Gets the WebResource associated with this endpoint.
     * @return The WebResource
     */
    public WebResource getWebResource() {
        return _resource;
    }

    /**
     * Sets the WebResource associated with this endpoint.
     * @param resource The resource
     */
    public void setWebResource(WebResource resource) {
        _resource = resource;
    }

    @Override
    public void start() {
        _resource.start(this);
    }

    @Override
    public void stop() {
        _resource.stop(this);
    }
}
