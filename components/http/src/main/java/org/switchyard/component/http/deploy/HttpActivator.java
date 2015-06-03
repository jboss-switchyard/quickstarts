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

package org.switchyard.component.http.deploy;

import javax.xml.namespace.QName;

import org.switchyard.component.http.InboundHandler;
import org.switchyard.component.http.OutboundHandler;
import org.switchyard.component.http.config.model.HttpBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.ServiceHandler;


/**
 * HTTP Activator.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class HttpActivator extends BaseActivator {

    static final String HTTP_TYPE = "http";
    private Configuration _environment;

    /**
     * Creates a new activator for HTTP service.
     */
    public HttpActivator() {
        super(HTTP_TYPE);
    }

    @Override
    public ServiceHandler activateBinding(QName name, BindingModel config) {
        HttpBindingModel binding = (HttpBindingModel)config;
        binding.setEnvironment(_environment);

        if (binding.isServiceBinding()) {
            return new InboundHandler(binding, getServiceDomain());
        } else {
            return new OutboundHandler(binding, getServiceDomain());
        }
    }

    @Override
    public void deactivateBinding(QName name, ServiceHandler handler) {
        // Nothing to do here
    }

    /**
     * Set the Environment configuration for the activator.
     * @param config The global environment configuration.
     */
    public void setEnvironment(Configuration config) {
        _environment = config;
    }
}
