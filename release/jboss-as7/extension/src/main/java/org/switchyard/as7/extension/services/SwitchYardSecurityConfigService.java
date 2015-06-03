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
package org.switchyard.as7.extension.services;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;
import org.switchyard.as7.extension.CommonAttributes;
import org.switchyard.as7.extension.services.SwitchYardSecurityConfigService.SecurityConfig;
import org.switchyard.common.lang.Strings;
import org.switchyard.security.context.SecurityContext;
import org.switchyard.security.crypto.PrivateCrypto;
import org.switchyard.security.crypto.PublicCrypto;
import org.switchyard.security.system.DefaultSystemSecurity;
import org.switchyard.security.system.SystemSecurity;

/**
 * The SwitchYard SecurityConfig service.
 * 
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class SwitchYardSecurityConfigService implements Service<SecurityConfig> {

    /**
     * Represents a SwitchYard SecurityConfig initializer service name.
     */
    public static final ServiceName SERVICE_NAME = ServiceName.of("SwitchYardSecurityConfigService");

    private final InjectedValue<SystemSecurity> _systemSecurity = new InjectedValue<SystemSecurity>();
    private final InjectedValue<Map> _injectedValues = new InjectedValue<Map>();

    private String _moduleId;
    private ModelNode _model;
    private SecurityConfig _securityConfig;

    /**
     * Constructs a SwitchYard SecurityConfig service.
     * 
     * @param moduleId the module identifier
     * @param model the Module's model operation
     */
    public SwitchYardSecurityConfigService(String moduleId, ModelNode model) {
        _moduleId = moduleId;
        _model = model;
    }

    @Override
    public SecurityConfig getValue() throws IllegalStateException,
            IllegalArgumentException {
        return _securityConfig;
    }

    @Override
    public void start(StartContext context) throws StartException {
        ModelNode propertiesModel = _model.hasDefined(CommonAttributes.PROPERTIES) ? _model.get(CommonAttributes.PROPERTIES) : null;
        Properties securityProps = toProperties(propertiesModel);
        _securityConfig = new SecurityConfig(securityProps);
        DefaultSystemSecurity systemSecurity = (DefaultSystemSecurity)getSystemSecurity().getValue();
        if (SecurityContext.class.getName().equals(_moduleId)) {
            String timeoutMillis = Strings.trimToNull(securityProps.getProperty("timeoutMillis"));
            if (timeoutMillis != null) {
                systemSecurity.setSecurityContextTimeoutMillis(Long.valueOf(timeoutMillis));
            }
        }
        if (PrivateCrypto.class.getName().equals(_moduleId)) {
            systemSecurity.setPrivateCrypto(new PrivateCrypto(securityProps));
        }
        if (PublicCrypto.class.getName().equals(_moduleId)) {
            systemSecurity.setPublicCrypto(new PublicCrypto(securityProps));
        }
    }

    private Properties toProperties(ModelNode propertiesModel) {
        Properties properties = new Properties();
        if (propertiesModel != null) {
            Set<String> names = propertiesModel.keys();
            if (names != null) {
                for (String name : names) {
                    String value = propertiesModel.get(name).asString();
                    if (value.startsWith(CommonAttributes.DOLLAR)) {
                        String key = value.substring(1);
                        String injectedValue = (String)_injectedValues.getValue().get(key);
                        if (injectedValue != null) {
                            properties.setProperty(name, injectedValue);
                        }
                    } else {
                        properties.setProperty(name, value);
                    }
                }
            }
        }
        return properties;
    }

    @Override
    public void stop(StopContext context) {
    }

    /**
     * SystemSecurity injection point.
     * 
     * @return injected SystemSecurity
     */
    public InjectedValue<SystemSecurity> getSystemSecurity() {
        return _systemSecurity;
    }

    /**
     * Injection point for injectValues.
     * 
     * @return a map of injected values
     */
    public InjectedValue<Map> getInjectedValues() {
        return _injectedValues;
    }

    /**
     * SecurityConfig.
     */
    public static final class SecurityConfig {
        private final Properties _properties;
        /**
         * Creates a new SecurityConfig with the specified properties.
         * @param properties the properties
         */
        public SecurityConfig(Properties properties) {
            _properties = properties;
        }
        /**
         * Gets the properties.
         * @return the properties
         */
        public Properties getProperties() {
            return _properties;
        }
    }

}
