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

package org.switchyard.component.resteasy.util;

import javax.ws.rs.core.MultivaluedMap;

import org.switchyard.component.resteasy.composer.RESTEasyBindingData;

/**
 * Client Invoker interface for RESTEasy.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public interface MethodInvoker {

    /**
     * Invokes the JAX-RS method.
     *
     * @param args the method arguments
     * @param headers the HTTP headers to be set on the request
     * @return the method's response entity and headers wrapped in RESTEasyBindingData
     */
    RESTEasyBindingData invoke(Object[] args, MultivaluedMap<String, String> headers);
}
