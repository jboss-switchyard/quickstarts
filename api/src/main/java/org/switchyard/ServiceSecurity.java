/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard;

import java.util.Map;
import java.util.Set;

/**
 * ServiceSecurity.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public interface ServiceSecurity {

    /**
     * The default name ("default").
     */
    public static final String DEFAULT_NAME = "default";

    /**
     * The default security domain ("other").
     */
    public static final String DEFAULT_SECURITY_DOMAIN = "other";

    /**
     * Gets the name.
     * @return the name
     */
    public String getName();

    /**
     * Gets the CallbackHandler class.
     * @return the CallbackHandler class
     */
    public Class<?> getCallbackHandler();

    /**
     * Gets the properties.
     * @return the properties
     */
    public Map<String,String> getProperties();

    /**
     * Gets the roles allowed.
     * @return the roles allowed
     */
    public Set<String> getRolesAllowed();

    /**
     * Gets the run as.
     * @return the run as
     */
    public String getRunAs();

    /**
     * Gets the security domain.
     * @return the security domain
     */
    public String getSecurityDomain();

}
