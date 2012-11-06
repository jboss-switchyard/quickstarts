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
package org.switchyard.security;

import java.util.ServiceLoader;

import org.switchyard.ServiceSecurity;

/**
 * SecurityProvider.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class SecurityProvider {

    private static final SecurityProvider INSTANCE;
    static {
        ServiceLoader<SecurityProvider> services = ServiceLoader.load(SecurityProvider.class, SecurityProvider.class.getClassLoader());
        INSTANCE = services.iterator().next();
    }

   /**
    * Authenticates the SecurityContext for the ServiceSecurity.
    * @param serviceSecurity the ServiceSecurity
    * @param securityContext the SecurityContext
    * @return whether authentication was successful
    */
   public abstract boolean authenticate(ServiceSecurity serviceSecurity, SecurityContext securityContext);

   /**
    * Gets the singleton instance of the SecurityProvider.
    * @return the singleton instance of the SecurityProvider
    */
   public static final SecurityProvider instance() {
       return INSTANCE;
   }

}
