/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.switchyard.component.camel.common.deploy;

import javax.xml.namespace.QName;

import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.camel.common.composer.CamelComposition;
import org.switchyard.component.camel.common.handler.InboundHandler;
import org.switchyard.component.camel.common.handler.OutboundHandler;
import org.switchyard.component.camel.common.model.CamelBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.ServiceHandler;

/**
 * Activator for handling camel bindings on both, service and reference, sides.
 * 
 * @author Lukasz Dywicki
 */
public class BaseBindingActivator extends BaseActivator {

    private Configuration _environment;
    private SwitchYardCamelContext _camelContext;

    protected BaseBindingActivator(SwitchYardCamelContext context, String ... types) {
        super(types);
        _camelContext = context;
    }

    /**
     * Specify environment configuration for binding.
     * 
     * @param config Environment settings.
     */
    public void setEnvironment(Configuration config) {
        _environment = config;
    }

    @Override
    public ServiceHandler activateBinding(QName serviceName, BindingModel config) {
        CamelBindingModel binding = (CamelBindingModel) config;
        binding.setEnvironment(_environment);

        if (binding.isServiceBinding()) {
            return new InboundHandler(binding, _camelContext, serviceName);
        } else {
            return new OutboundHandler(binding.getComponentURI().toString(), _camelContext, CamelComposition
                .getMessageComposer(binding));
        }
    }

    @Override
    public void deactivateBinding(QName name, ServiceHandler handler) {
        // anything to do here?
    }

    @Override
    public void deactivateService(QName name, ServiceHandler handler) {
        // anything to do here?
    }

    /**
     * Gets the {@link CamelContext} used by this Activator.
     * 
     * @return CamelContext the {@link CamelContext} used by this Activator.
     */
    public SwitchYardCamelContext getCamelContext() {
        return _camelContext;
    }

}
