/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.common.rules.util.drools;

import java.util.Map;
import java.util.Properties;

import org.drools.base.MapGlobalResolver;
import org.drools.builder.conf.ClassLoaderCacheOption;
import org.drools.impl.EnvironmentFactory;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;

/**
 * Drools Environment Utilities.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Environments {

    /**
     * Creates a new Environment.
     * @return the environment
     */
    public static Environment getEnvironment() {
        return getEnvironment((Map<String, Object>)null);
    }

    /**
     * Creates a new Environment with the specified component implementation config.
     * @param cic the specified component implementation config
     * @return the environment
     */
    public static Environment getEnvironment(ComponentImplementationConfig cic) {
        Map<String, Object> overrides = cic != null ? cic.getEnvironmentOverrides() : null;
        return getEnvironment(overrides);
    }

    /**
     * Creates a new Environment with the specified overrides.
     * @param overrides the specified overrides
     * @return the environment
     */
    public static Environment getEnvironment(Map<String, Object> overrides) {
        Environment env = EnvironmentFactory.newEnvironment();
        // set the default global resolver
        env.set(EnvironmentName.GLOBALS, new MapGlobalResolver());
        // apply any overrides
        if (overrides != null) {
            for (Map.Entry<String, Object> entry : overrides.entrySet()) {
                env.set(entry.getKey(), entry.getValue());
            }
        }
        return env;
    }

    /**
     * Creates a new Properties.
     * @return the properties
     */
    public static Properties getProperties() {
        return getProperties((Properties)null);
    }

    /**
     * Creates a new Properties with the specified component implementation config.
     * @param cic the specified component implementation config
     * @return the properties
     */
    public static Properties getProperties(ComponentImplementationConfig cic) {
        Properties overrides = cic != null ? cic.getPropertiesOverrides() : null;
        return getProperties(overrides);
    }

    /**
     * Creates a new Properties with the specified overrides.
     * @param overrides the specified overrides
     * @return the properties
     */
    public static Properties getProperties(Properties overrides) {
        Properties props = new Properties();
        // If this isn't false, then all rules' LHS object conditions will not match on redeploys!
        // (since objects are only equal if they're classloaders are also equal - and they're not on redeploys)
        props.setProperty(ClassLoaderCacheOption.PROPERTY_NAME, Boolean.FALSE.toString());
        // apply any overrides
        if (overrides != null) {
            for (Object o : overrides.keySet()) {
                String key = (String)o;
                props.setProperty(key, overrides.getProperty(key));
            }
        }
        return props;
    }

    private Environments() {}

}
