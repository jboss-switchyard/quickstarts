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

import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Resolves properties from System Properties.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class SystemPropertiesPropertyResolver extends PropertiesPropertyResolver {

    private static final Logger LOGGER = Logger.getLogger(SystemPropertiesPropertyResolver.class);

    private static final SystemPropertiesPropertyResolver INSTANCE;
    static {
        Properties systemProperties;
        try {
            systemProperties = System.getProperties();
        } catch (SecurityException se) {
            LOGGER.error("SecurityException while getting System Properties; will default to empty Properties", se);
            systemProperties = new Properties();
        }
        INSTANCE = new SystemPropertiesPropertyResolver(systemProperties);
    }

    private SystemPropertiesPropertyResolver(Properties systemProperties) {
        super(systemProperties);
    }

    /**
     * Returns the singleton instance.
     * @return the singleton instance
     */
    public static final SystemPropertiesPropertyResolver instance() {
        return INSTANCE;
    }

}
