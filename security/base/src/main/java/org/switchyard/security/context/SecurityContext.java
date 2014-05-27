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
package org.switchyard.security.context;

import java.io.Serializable;
import java.security.Principal;
import java.util.Set;
import java.util.UUID;

import javax.security.auth.Subject;

import org.switchyard.security.credential.Credential;

/**
 * SecurityContext.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public interface SecurityContext extends Serializable {

    /**
     * Whether this security context is valid, within the current system and without having expired.
     * @param systemUUID the system security universally unique identifier
     * @return whether this security context is valid
     */
    public boolean isValid(UUID systemUUID);

    /**
     * Gets the Credentials.
     * @return the Credentials
     */
    public Set<Credential> getCredentials();

    /**
     * Gets the Credentials that match the type.
     * @param <T> the type
     * @param clazz the class
     * @return the Credentials that match the type
     */
    public <T extends Credential> Set<T> getCredentials(Class<T> clazz);

    /**
     * Clears the credentials.
     */
    public void clearCredentials();

    /**
     * Gets the Subject for the security domain, creating one if one did not already exist.
     * @param securityDomain the security domain
     * @return the Subject for the security domain
     */
    public Subject getSubject(String securityDomain);

    /**
     * Gets the Subject for the security domain.
     * @param securityDomain the security domain
     * @param create create one if one did not already exist
     * @return the Subject for the security domain, or null if create is false and one didn't already exist
     */
    public Subject getSubject(String securityDomain, boolean create);

    /**
     * Clears the Subject for the security domain.
     * @param securityDomain the security domain
     */
    public void clearSubject(String securityDomain);

    /**
     * Gets the caller Principal for the security domain.
     * @param securityDomain the security domain
     * @return the caller Principal
     */
    public Principal getCallerPrincipal(String securityDomain);

    /**
     * Is the caller in the role name for the security domain.
     * @param roleName the role name
     * @param securityDomain the security domain
     * @return successful check
     */
    public boolean isCallerInRole(String roleName, String securityDomain);

}
