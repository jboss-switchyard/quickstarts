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

package org.switchyard.component.resteasy.deploy;

import javax.xml.namespace.QName;

import org.switchyard.component.resteasy.InboundHandler;
import org.switchyard.component.resteasy.OutboundHandler;
import org.switchyard.component.resteasy.config.model.RESTEasyBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.ServiceHandler;


/**
 * RESTEasy Activator.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class RESTEasyActivator extends BaseActivator {

    static final String RESTEASY_TYPE = "rest";
    private Configuration _environment;

    /**
     * Creates a new activator for RESTEasy service.
     */
    public RESTEasyActivator() {
        super(RESTEASY_TYPE);
    }

    @Override
    public ServiceHandler activateBinding(QName name, BindingModel config) {
        RESTEasyBindingModel binding = (RESTEasyBindingModel)config;
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
