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
package org.switchyard.deploy.osgi.internal.http;

import org.osgi.service.http.HttpService;
import org.switchyard.component.common.Endpoint;

/**
 * An OSGi HTTP endpoint.
 */
public class OsgiHttpEndpoint implements Endpoint {

    private HttpService _httpService;
    private String _alias;

    public OsgiHttpEndpoint(HttpService service, String alias) {
        _httpService = service;
        _alias = alias;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        _httpService.unregister(_alias);
    }

}
