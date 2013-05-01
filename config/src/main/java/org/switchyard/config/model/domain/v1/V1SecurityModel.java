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
package org.switchyard.config.model.domain.v1;

import static org.switchyard.config.model.property.PropertiesModel.PROPERTIES;
import static org.switchyard.config.model.switchyard.SwitchYardModel.DEFAULT_NAMESPACE;

import java.util.Set;

import javax.xml.namespace.QName;

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
     */
    public V1SecurityModel() {
        super(new QName(DEFAULT_NAMESPACE, SECURITY));
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
        return getModelAttribute(RUN_AS);
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
        return getModelAttribute(SECURITY_DOMAIN);
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
