/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.deploy.osgi.internal;

import org.osgi.framework.BundleContext;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceSecurity;
import org.switchyard.bus.camel.CamelExchangeBus;
import org.switchyard.config.model.property.PropertiesModel;
import org.switchyard.config.model.property.PropertyModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.internal.DomainImpl;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.internal.validate.BaseValidatorRegistry;
import org.switchyard.security.service.ServiceDomainSecurity;
import org.switchyard.transform.TransformerRegistry;
import org.switchyard.validate.ValidatorRegistry;

import javax.xml.namespace.QName;

import java.util.Map;

/**
 */
public class OsgiDomainManager extends ServiceDomainManager {

    private final SwitchyardExtender extender;

    public OsgiDomainManager(SwitchyardExtender extender) {
        this.extender = extender;
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
        PropertiesModel properties = getProperties(switchyardConfig);
        if (properties != null) {
            for (PropertyModel property : properties.getProperties()) {
                domain.setProperty(property.getName(), property.getValue());
            }
        }

        // now that all resources and properties are set, init the domain
        domain.init();

        return domain;
    }

}
