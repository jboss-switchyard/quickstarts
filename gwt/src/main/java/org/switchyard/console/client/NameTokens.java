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

package org.switchyard.console.client;

import com.google.gwt.http.client.URL;

/**
 * NameTokens
 * 
 * SwitchYard specific path tokens.
 * 
 * @author Rob Cernich
 */
public class NameTokens extends org.jboss.as.console.client.core.NameTokens {

    /** The path for the main SwitchYard view. */
    public static final String SWITCH_YARD_PRESENTER = "switchyard";
    /** The subpath for the SwitchYard system configuration view. */
    public static final String SYSTEM_CONFIG_PRESENTER = "system";
    /** The subpath for the SwitchYard component configuration view. */
    public static final String COMPONENT_CONFIG_PRESENTER = "component";
    /** The subpath for the SwitchYard application configuration view. */
    public static final String APPLICATION_CONFIG_PRESENTER = "application";
    /** The subpath for the SwitchYard service view. */
    public static final String SERVICE_CONFIG_PRESENTER = "service";

    /**
     * Helper method to create URL to a specific application page.
     * 
     * @param applicationName the name of the application page.
     * @return a token URL for displaying the application's page.
     */
    public static String createApplicationLink(String applicationName) {
        return "switchyard/application;application=" + URL.encode(applicationName);
    }

    /**
     * Helper method to create URL to a specific component page.
     * 
     * @param componentName the name of the component page.
     * @return a token URL for displaying the component page.
     */
    public static String createComponentLink(String componentName) {
        return "switchyard/component;component=" + URL.encode(componentName);
    }

    /**
     * Helper method to create URL to a specific service page.
     * 
     * @param serviceName the name of the service.
     * @param applicationName the name of the service's containing application.
     * @return a token URL for displaying the service page.
     */
    public static String createServiceLink(String serviceName, String applicationName) {
        return "switchyard/service;service=" + URL.encode(serviceName) + ";application=" + URL.encode(applicationName);
    }

}
