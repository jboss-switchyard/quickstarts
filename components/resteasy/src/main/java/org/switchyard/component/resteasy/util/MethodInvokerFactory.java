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

import java.lang.reflect.Method;

import org.switchyard.component.resteasy.config.model.RESTEasyBindingModel;

/**
 * A factory class to create MethodInvoker for the resteasy outbound invocation.
 */
public interface MethodInvokerFactory {

    /**
     * Creates a MethodInvoker instance.
     * @param basePath base path
     * @param resourceClass resource class
     * @param method method
     * @param model RESTEasyBindingModel
     * @return MethodInvoker
     */
    public MethodInvoker createInvoker(String basePath, Class<?> resourceClass, Method method, RESTEasyBindingModel model);
}
