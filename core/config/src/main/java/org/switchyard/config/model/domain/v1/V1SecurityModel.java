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
package org.switchyard.config.model.domain.v1;

import static org.switchyard.config.model.property.PropertiesModel.PROPERTIES;

import java.util.Set;

import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.Classes;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.domain.SecuritiesModel;
import org.switchyard.config.model.domain.SecurityModel;
import org.switchyard.config.model.property.PropertiesModel;

/**
 * The 1st version SecurityModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1SecurityModel extends BaseNamedModel implements SecurityModel {

    private static final String CALLBACK_HANDLER = "callbackHandler";
    private static final String ROLES_ALLOWED = "rolesAllowed";
    private static final String RUN_AS = "runAs";
    private static final String SECURITY_DOMAIN = "securityDomain";

    private PropertiesModel _properties;

    /**
     * Creates a new V1SecurityModel.
     * @param namespace namespace
     */
    public V1SecurityModel(String namespace) {
        super(namespace, SECURITY);
        setModelChildrenOrder(PROPERTIES);
    }

    /**
     * Creates a new V1SecurityModel.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1SecurityModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(PROPERTIES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecuritiesModel getSecurities() {
        return (SecuritiesModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getCallbackHandler(ClassLoader loader) {
        String c = Strings.trimToNull(getModelAttribute(CALLBACK_HANDLER));
        return c != null ? Classes.forName(c, loader) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecurityModel setCallbackHandler(Class<?> clazz) {
        String c = clazz != null ? clazz.getName() : null;
        setModelAttribute(CALLBACK_HANDLER, c);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getRolesAllowed() {
        String ra = getModelAttribute(ROLES_ALLOWED);
        return Strings.uniqueSplitTrimToNull(ra, ",");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecurityModel setRolesAllowed(Set<String> rolesAllowed) {
        String[] ra = rolesAllowed != null ? rolesAllowed.toArray(new String[rolesAllowed.size()]) : null;
        setModelAttribute(ROLES_ALLOWED, Strings.concat(",", ra));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRunAs() {
        String runAs = getModelAttribute(RUN_AS);
        return Strings.trimToNull(runAs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecurityModel setRunAs(String runAs) {
        setModelAttribute(RUN_AS, runAs);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized PropertiesModel getProperties() {
        if (_properties == null) {
            _properties = (PropertiesModel)getFirstChildModel(PROPERTIES);
        }
        return _properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecurityModel setProperties(PropertiesModel properties) {
        setChildModel(properties);
        _properties = properties;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSecurityDomain() {
        String securityDomain = getModelAttribute(SECURITY_DOMAIN);
        return Strings.trimToNull(securityDomain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecurityModel setSecurityDomain(String securityDomainName) {
        setModelAttribute(SECURITY_DOMAIN, securityDomainName);
        return this;
    }

}
