/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.deploy.osgi.internal;

import java.util.Map;

import javax.xml.namespace.QName;

import org.osgi.framework.BundleContext;
import org.switchyard.ServiceDomain;
import org.switchyard.bus.camel.CamelExchangeBus;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.internal.DomainImpl;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.internal.validate.BaseValidatorRegistry;
import org.switchyard.security.service.ServiceDomainSecurity;
import org.switchyard.transform.TransformerRegistry;
import org.switchyard.validate.ValidatorRegistry;

/**
 */
public class OsgiDomainManager extends ServiceDomainManager {

    @SuppressWarnings("unused")
    private final SwitchYardExtender _extender;

    public OsgiDomainManager(SwitchYardExtender extender) {
        _extender = extender;
    }

    public ServiceDomain createDomain(BundleContext bundleContext, QName domainName, SwitchYardModel switchyardConfig) {
        TransformerRegistry transformerRegistry = new BaseTransformerRegistry();
        ValidatorRegistry validatorRegistry = new BaseValidatorRegistry();

        OsgiSwitchYardCamelContextImpl camelContext = new OsgiSwitchYardCamelContextImpl(bundleContext);
        CamelExchangeBus bus = new CamelExchangeBus(camelContext);

        ServiceDomainSecurity serviceSecurities = getServiceDomainSecurity(switchyardConfig);

        DomainImpl domain = new DomainImpl(
                domainName, getRegistry(), bus, transformerRegistry,
                validatorRegistry, getEventManager(), serviceSecurities);
        camelContext.setServiceDomain(domain);

        /*if (switchyardConfig != null) {
            domain.getHandlers().addAll(getDomainHandlers(switchyardConfig.getDomain()));
        }*/
        
        // set properties on the domain
        Map<String, String> properties = getDomainProperties(switchyardConfig);
        for (Map.Entry<String, String> property : properties.entrySet()) {
            domain.setProperty(property.getKey(), property.getValue());
        }

        // now that all resources and properties are set, init the domain
        domain.init();

        return domain;
    }

}
