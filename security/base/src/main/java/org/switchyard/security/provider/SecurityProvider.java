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
package org.switchyard.security.provider;

import org.switchyard.ServiceSecurity;
import org.switchyard.security.context.SecurityContext;

/**
 * SecurityProvider.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public interface SecurityProvider {
    
   /**
    * Authenticates the SecurityContext for the ServiceSecurity.
    * @param serviceSecurity the ServiceSecurity
    * @param securityContext the SecurityContext
    * @return whether authentication was successful
    */
   boolean authenticate(ServiceSecurity serviceSecurity, SecurityContext securityContext);

   /**
    * Propagates any existing container-specific (most likely thread-local) security information into the SecurityContext.
    * @param serviceSecurity the ServiceSecurity
    * @param securityContext the SecurityContext
    * @return whether propagation was successful
    */
   boolean propagate(ServiceSecurity serviceSecurity, SecurityContext securityContext);

   /**
     * Adds the ServiceSecurity's runAs Role to the SecurityContext's Subject.
     * @param serviceSecurity the ServiceSecurity
     * @param securityContext the SecurityContext
     * @return whether adding the runAs Role was successful
    */
   boolean addRunAs(ServiceSecurity serviceSecurity, SecurityContext securityContext);

   /**
    * Checks if the Subject associated in the SecurityContext has at least one of the allowed roles in the ServiceSecurity.
    * @param serviceSecurity the ServiceSecurity
    * @param securityContext the SecurityContext
    * @return whether the allowed roles check was successful
    */
   boolean checkRolesAllowed(ServiceSecurity serviceSecurity, SecurityContext securityContext);

   /**
    * Clears the SecurityContext and any underlying SecurityContextAssociation.
    * @param serviceSecurity the ServiceSecurity
    * @param securityContext the SecurityContext
    * @return whether the clear was successful
    */
   boolean clear(ServiceSecurity serviceSecurity, SecurityContext securityContext);
}
