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
package org.switchyard.common.property;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Resolves properties from System Properties and the System environment.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class SystemPropertyResolver implements PropertyResolver {

    /** The singleton instance. */
    public static final SystemPropertyResolver INSTANCE = new SystemPropertyResolver();

    /** The singleton constructor. */
    private SystemPropertyResolver() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public final Object resolveProperty(final String key) {
        return key != null ? AccessController.doPrivileged(new PrivilegedAction<String>() {
           public String run() {
               String value = System.getProperty(key);
               if (value == null && key.startsWith("env.")) {
                   value =  System.getenv(key.substring(4));
               }
               return value;
           }
        }) : null;
    }

}
