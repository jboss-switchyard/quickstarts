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
package org.switchyard.deploy.osgi.internal.resteasy;

import java.lang.reflect.Method;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.switchyard.component.resteasy.config.model.RESTEasyBindingModel;
import org.switchyard.component.resteasy.util.ClientInvoker;
import org.switchyard.component.resteasy.util.MethodInvoker;
import org.switchyard.component.resteasy.util.MethodInvokerFactory;

/**
 * A factory class to create MethodInvoker with registering RESTEasy providers explicitly.
 * This is a workaround for https://issues.jboss.org/browse/RESTEASY-640
 */
public class OsgiMethodInvokerFactory implements MethodInvokerFactory {

    @Override
    public MethodInvoker createInvoker(String basePath, Class<?> resourceClass, Method method, RESTEasyBindingModel model) {
        ClientInvoker invoker = new ClientInvoker(basePath, resourceClass, method, model);
        ResteasyProviderFactory repFactory = invoker.getResteasyProviderFactory();
        for (Class<?> provider : RESTEasyProviders.PROVIDERS) {
            repFactory.registerProvider(provider);
        }
        return invoker;
    }

}
