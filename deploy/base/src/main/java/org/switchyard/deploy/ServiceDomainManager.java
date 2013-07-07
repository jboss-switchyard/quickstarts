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

package org.switchyard.deploy;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceSecurity;
import org.switchyard.bus.camel.CamelExchangeBus;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.config.model.domain.DomainModel;
import org.switchyard.config.model.domain.SecuritiesModel;
import org.switchyard.config.model.domain.SecurityModel;
import org.switchyard.config.model.property.PropertiesModel;
import org.switchyard.config.model.property.PropertyModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.internal.DefaultServiceRegistry;
import org.switchyard.internal.DefaultServiceSecurity;
import org.switchyard.internal.DomainImpl;
import org.switchyard.internal.EventManager;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.internal.validate.BaseValidatorRegistry;
import org.switchyard.spi.ServiceRegistry;
import org.switchyard.transform.TransformerRegistry;
import org.switchyard.validate.ValidatorRegistry;

/**
 * {@link org.switchyard.ServiceDomain} manager class.
 * <p/>
 * Currently supports a flat ServiceDomain model with a ServiceDomain per application/deployment,
 * all managed from this container level bean.  Deployments are supplied with a {@link DomainProxy}
 * instance which can first delegate service lookup to the application's own domain, but on lookup
 * failure, can then delegate to this class in order to continue the lookup across all application
 * ServiceDomain's managed by the container.
 * <p/>
 * This model does not yet support the notion of multiple isolated ServiceDomains.  This class will
 * change or go away.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ServiceDomainManager {

    /**
     * Root domain property.
     */
    public static final QName ROOT_DOMAIN = new QName("org.switchyard.domains.root");
    
    private static Logger _log = Logger.getLogger(ServiceDomainManager.class);

    // Share the same service registry and bus across domains to give visibility 
    // to registered services across application domains
    private ServiceRegistry _registry = new DefaultServiceRegistry();
    private EventManager _eventManager = new EventManager();
    
    /**
     * Constructs a new ServiceDomainManager.
     */
    public ServiceDomainManager() {
         
    }
    
    /**
     * Create a ServiceDomain instance.
     * <p/>
     * Uses {@link #ROOT_DOMAIN} as the domain name.
     * @return The ServiceDomain instance.
     */
    public ServiceDomain createDomain() {
        return createDomain(ROOT_DOMAIN, null);
    }

    /**
     * Create a ServiceDomain instance.
     * @param domainName The domain name.
     * @param switchyardConfig The SwitchYard configuration.
     * @return The ServiceDomain instance.
     */
    public ServiceDomain createDomain(QName domainName, SwitchYardModel switchyardConfig) {
        TransformerRegistry transformerRegistry = new BaseTransformerRegistry();
        ValidatorRegistry validatorRegistry = new BaseValidatorRegistry();

        SwitchYardCamelContext camelContext = new SwitchYardCamelContext();
        CamelExchangeBus bus = new CamelExchangeBus(camelContext);

        Map<String, ServiceSecurity> serviceSecurities = getServiceSecurities(switchyardConfig);

        DomainImpl domain = new DomainImpl(
                domainName, _registry, bus, transformerRegistry, validatorRegistry, _eventManager, serviceSecurities);
        camelContext.setServiceDomain(domain);

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
    
    /**
     * Return the shared EventManager used for all ServiceDomain instances.
     * @return EventManager instance
     */
    public EventManager getEventManager() {
        return _eventManager;
    }
    
    private Map<String, ServiceSecurity> getServiceSecurities(SwitchYardModel switchyard) {
        Map<String, ServiceSecurity> map = new HashMap<String, ServiceSecurity>();
        if (switchyard != null) {
            DomainModel domain = switchyard.getDomain();
            if (domain != null) {
                SecuritiesModel securities = domain.getSecurities();
                if (securities != null) {
                    for (SecurityModel security : securities.getSecurities()) {
                        if (security != null) {
                            PropertiesModel properties = security.getProperties();
                            ServiceSecurity value = new DefaultServiceSecurity()
                                .setName(security.getName())
                                .setCallbackHandler(security.getCallbackHandler(getClass().getClassLoader()))
                                .setProperties(properties != null ? properties.toMap() : null)
                                .setRolesAllowed(security.getRolesAllowed())
                                .setRunAs(security.getRunAs())
                                .setSecurityDomain(security.getSecurityDomain());
                            String key = value.getName();
                            if (!map.containsKey(key)) {
                                map.put(key, value);
                            } else {
                                throw new IllegalStateException("Duplicate security configuration names calculated: " + key);
                            }
                        }
                    }
                }
            }
        }
        return map;
    }
    
    private PropertiesModel getProperties(SwitchYardModel config) {
        if (config == null || config.getDomain() == null) {
            return null;
        } else {
            return config.getDomain().getProperties();
        }
    }
}
