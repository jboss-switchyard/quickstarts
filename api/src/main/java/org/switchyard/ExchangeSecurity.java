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
package org.switchyard;

import java.security.Principal;

/**
 * ExchangeSecurity.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public interface ExchangeSecurity {

    /**
     * Gets the security domain.
     * @return the security domain
     */
    public String getSecurityDomain();

    // TODO: investigate need and safety of exposing the Subject
    // public Subject getSubject();

    /**
     * Gets the caller Principal.
     * @return the caller Principal
     */
    public Principal getCallerPrincipal();

    /**
     * Is the caller in the role name.
     * @param roleName the role name
     * @return successful check
     */
    public boolean isCallerInRole(String roleName);

}
