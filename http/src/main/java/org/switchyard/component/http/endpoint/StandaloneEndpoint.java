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
 
package org.switchyard.component.http.endpoint;

import org.jboss.com.sun.net.httpserver.HttpContext;

/**
 * A standalone HTTP endpoint.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class StandaloneEndpoint implements Endpoint {

    private HttpContext _httpContext;
    private static Long _contextCount = 0L;

    /**
     * Construct a StandaloneEndpoint with the given context.
     * @param context The HttpContext
     */
    public StandaloneEndpoint(final HttpContext context) {
        _httpContext = context;
        incrementContextCount();
    }

    private synchronized void incrementContextCount() {
        _contextCount++;
    }
    
    private synchronized void decrementContextCount() {
        _contextCount--;
    }
    
    /**
     * {@inheritDoc}
     */
    public void stop() {
        if (_httpContext != null) {
            _httpContext.getServer().removeContext(_httpContext);
            decrementContextCount();
        }
    }
}
