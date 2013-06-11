/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
 * details. You should have received a copy of the GNU Lesser General Public 
 * License, v.2.1 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301, USA.
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
