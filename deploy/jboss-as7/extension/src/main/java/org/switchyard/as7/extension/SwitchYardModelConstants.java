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
package org.switchyard.as7.extension;

/**
 * SwitchYardModelConstants
 * 
 * Constants used for AS7 management model integration.
 * 
 * @author Rob Cernich
 */
public final class SwitchYardModelConstants {

    // operations
    /**
     * Constant for /subsystem=switchyard/:get-version operation.
     */
    public static final String GET_VERSION = "get-version";
    /**
     * Constant for /subsystem=switchyard/:list-applications operation.
     */
    public static final String LIST_APPLICATIONS = "list-applications";
    /**
     * Constant for /subsystem=switchyard/:list-components operation.
     */
    public static final String LIST_COMPONENTS = "list-components";
    /**
     * Constant for /subsystem=switchyard/:list-services operation.
     */
    public static final String LIST_SERVICES = "list-services";
    /**
     * Constant for /subsystem=switchyard/:read-application operation.
     */
    public static final String READ_APPLICATION = "read-application";
    /**
     * Constant for /subsystem=switchyard/:read-component operation.
     */
    public static final String READ_COMPONENT = "read-component";
    /**
     * Constant for /subsystem=switchyard/:read-service operation.
     */
    public static final String READ_SERVICE = "read-service";
    
    // nodes
    /**
     * Constant for model key: application.
     */
    public static final String APPLICATION = "application";
    /**
     * Constant for model key: application-name.
     */
    public static final String APPLICATION_NAME = "application-name";
    /**
     * Constant for model key: componentServices.
     */
    public static final String COMPONENT_SERVICES = "componentServices";
    /**
     * Constant for model key: config-schema.
     */
    public static final String CONFIG_SCHEMA = "configSchema";
    /**
     * Constant for model key: configuration.
     */
    public static final String CONFIGURATION = "configuration";
    /**
     * Constant for model key: from.
     */
    public static final String FROM = "from";
    /**
     * Constant for model key: implementation.
     */
    public static final String IMPLEMENTATION = "implementation";
    /**
     * Constant for model key: gateway.
     */
    public static final String GATEWAY = "gateway";
    /**
     * Constant for model key: gateways.
     */
    public static final String GATEWAYS = "gateways";
    /**
     * Constant for model key: promotedService.
     */
    public static final String PROMOTED_SERVICE = "promotedService";
    /**
     * Constant for model key: references.
     */
    public static final String REFERENCES = "references";
    /**
     * Constant for model key: service-name.
     */
    public static final String SERVICE_NAME = "service-name";
    /**
     * Constant for model key: services.
     */
    public static final String SERVICES = "services";
    /**
     * Constant for model key: to.
     */
    public static final String TO = "to";
    /**
     * Constant for model key: transformations.
     */
    public static final String TRANSFORMERS = "transformers";

    private SwitchYardModelConstants() {
    }

}
