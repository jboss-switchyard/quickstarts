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

import java.security.PrivilegedExceptionAction;

import org.switchyard.ServiceSecurity;
import org.switchyard.security.context.SecurityContext;

/**
 * SecurityProvider.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public interface SecurityProvider {

   /**
    * Performs a login with the SecurityContext for the ServiceSecurity.
    * @param serviceSecurity the ServiceSecurity
    * @param securityContext the SecurityContext
    * @return whether authentication was successful
    */
   public boolean authenticate(ServiceSecurity serviceSecurity, SecurityContext securityContext);

   /**
    * Populates the SecurityContext with any existing (possibly container-specific) security information.
    * @param serviceSecurity the ServiceSecurity
    * @param securityContext the SecurityContext
    */
   public void populate(ServiceSecurity serviceSecurity, SecurityContext securityContext);

   /**
    * Checks if the Subject associated in the SecurityContext has at least one of the allowed roles in the ServiceSecurity.
    * @param serviceSecurity the ServiceSecurity
    * @param securityContext the SecurityContext
    * @return whether the allowed roles check was successful
    */
   public boolean checkRolesAllowed(ServiceSecurity serviceSecurity, SecurityContext securityContext);

   /**
    * Runs the PrivilegedExceptionAction with the SecurityContext according to the runAs for the ServiceSecurity.
    * @param serviceSecurity the ServiceSecurity
    * @param securityContext the SecurityContext
    * @param action the PrivilegedExceptionAction
    * @param <T> the return value type
    * @return the return value
    * @throws Exception if a problem occurs
    */
   public <T> T runAs(ServiceSecurity serviceSecurity, SecurityContext securityContext, PrivilegedExceptionAction<T> action) throws Exception;

   /**
    * Clears the SecurityContext and any (possibly container-specific) association.
    * @param serviceSecurity the ServiceSecurity
    * @param securityContext the SecurityContext
    */
   public void clear(ServiceSecurity serviceSecurity, SecurityContext securityContext);

}
