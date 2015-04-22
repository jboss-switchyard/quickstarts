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
package org.switchyard.config.model.domain;

import java.util.Set;

import org.switchyard.config.model.NamedModel;
import org.switchyard.config.model.property.PropertiesModel;

/**
 * A Security Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface SecurityModel extends NamedModel {

    /**
     * The security XML element.
     */
    public static final String SECURITY = "security";

    /**
     * Gets the parent securities model.
     * @return the parent securities model
     */
    public SecuritiesModel getSecurities();

    /**
     * Gets the CallbackHandler class.
     * @param loader the ClassLoader to use
     * @return the CallbackHandler class
     */
    public Class<?> getCallbackHandler(ClassLoader loader);

    /**
     * Sets the CallbackHandler class.
     * @param callbackHandler the CallbackHandler class
     * @return this SecurityModel (useful for chaining)
     */
    public SecurityModel setCallbackHandler(Class<?> callbackHandler);

    /**
     * Gets the roles allowed.
     * @return the roles allowed
     */
    public Set<String> getRolesAllowed();

    /**
     * Sets the roles allowed.
     * @param rolesAllowed the roles allowed
     * @return this SecurityModel (useful for chaining)
     */
    public SecurityModel setRolesAllowed(Set<String> rolesAllowed);

    /**
     * Gets the run as.
     * @return the run as
     */
    public String getRunAs();

    /**
     * Sets the run as.
     * @param runAs the run as
     * @return this SecurityModel (useful for chaining)
     */
    public SecurityModel setRunAs(String runAs);

    /**
     * Gets the properties.
     * @return the properties
     */
    public PropertiesModel getProperties();

    /**
     * Sets the properties.
     * @param properties the properties
     * @return this SecurityModel (useful for chaining)
     */
    public SecurityModel setProperties(PropertiesModel properties);

    /**
     * Gets the security domain.
     * @return the security domain
     */
    public String getSecurityDomain();

    /**
     * Sets the security domain.
     * @param securityDomain the security domain
     * @return this SecurityModel (useful for chaining)
     */
    public SecurityModel setSecurityDomain(String securityDomain);

}
