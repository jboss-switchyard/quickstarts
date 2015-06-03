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
package org.switchyard.admin.base;

import javax.xml.namespace.QName;

import org.switchyard.admin.Binding;
import org.switchyard.deploy.Lifecycle;

/**
 * BaseBinding
 * 
 * Base implementation for {@link Binding}.
 * 
 * @author Rob Cernich
 */
public class BaseBinding extends BaseMessageMetricsAware implements Binding {

    private final BaseApplication _application;
    private final QName _serviceName;
    private final String _type;
    private final String _name;
    private final String _configuration;

    /**
     * Create a new BaseBinding.
     * 
     * @param application the containing application
     * @param serviceName the name of the service or reference providing this
     *            binding
     * @param type the binding's type (e.g. soap)
     * @param name the binding's name
     * @param configuration the binding's raw configuration
     */
    public BaseBinding(BaseApplication application, QName serviceName, String type, String name, String configuration) {
        _application = application;
        _serviceName = serviceName;
        _type = type;
        _name = name;
        _configuration = configuration;
    }

    @Override
    public String getType() {
        return _type;
    }

    @Override
    public String getConfiguration() {
        return _configuration;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public void start() {
        /*
         * TODO: should we verify that we could retrieve the service handler?
         * should we check the state first?
         */
        getGatewayLifecycle().start();
    }

    @Override
    public void stop() {
        /*
         * TODO: should we verify that we could retrieve the service handler?
         * should we check the state first?
         */
        getGatewayLifecycle().stop();
    }

    @Override
    public State getState() {
        final Lifecycle lifecycle = getGatewayLifecycle();
        return lifecycle == null ? State.NONE : lifecycle.getState();
    }

    private Lifecycle getGatewayLifecycle() {
        return _application.getDeployment().getGatwayLifecycle(_serviceName, _name);
    }
}
