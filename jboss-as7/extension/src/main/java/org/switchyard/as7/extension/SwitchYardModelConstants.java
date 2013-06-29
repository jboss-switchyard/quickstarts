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
     * Constant for /subsystem=switchyard/:list-references operation.
     */
    public static final String LIST_REFERENCES = "list-references";
    /**
     * Constant for /subsystem=switchyard/:list-services operation.
     */
    public static final String LIST_SERVICES = "list-services";
    /**
     * Constant for /subsystem=switchyard/:read-application operation.
     */
    public static final String READ_APPLICATION = "read-application";
    /**
     * Constant for /subsystem=switchyard/:read-reference operation.
     */
    public static final String READ_REFERENCE = "read-reference";
    /**
     * Constant for /subsystem=switchyard/:read-service operation.
     */
    public static final String READ_SERVICE = "read-service";
    /**
     * Constant for /subsystem=switchyard/:uses-artifact operation.
     */
    public static final String USES_ARTIFACT = "uses-artifact";
    /**
     * Constant for /subsystem=switchyard/:show-metrics operation.
     */
    public static final String SHOW_METRICS = "show-metrics";
    /**
     * Constant for /subsystem=switchyard/:reset-metrics operation.
     */
    public static final String RESET_METRICS = "reset-metrics";
    /**
     * Constant for /subsystem=switchyard/:stop-gateway operation.
     */
    public static final String STOP_GATEWAY = "stop-gateway";
    /**
     * Constant for /subsystem=switchyard/:start-gateway operation.
     */
    public static final String START_GATEWAY = "start-gateway";
    /**
     * Constant for /subsystem=switchyard/:update-throttling operation.
     */
    public static final String UPDATE_THROTTLING = "update-throttling";
    
    // nodes
    /**
     * Constant for model key: name.
     */
    public static final String NAME = "name";
    /**
     * Constant for model key: interface.
     */
    public static final String INTERFACE = "interface";
    /**
     * Constant for model key: type.
     */
    public static final String TYPE = "type";
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
     * Constant for model key: implementationConfiguration.
     */
    public static final String IMPLEMENTATION_CONFIGURATION = "implementationConfiguration";
    /**
     * Constant for model key: gateway.
     */
    public static final String GATEWAY = "gateway";
    /**
     * Constant for model key: gateways.
     */
    public static final String GATEWAYS = "gateways";
    /**
     * Constant for model key: operations.
     */
    public static final String OPERATIONS = "operations";
    /**
     * Constant for model key: promotedReference.
     */
    public static final String PROMOTED_REFERENCE = "promotedReference";
    /**
     * Constant for model key: promotedService.
     */
    public static final String PROMOTED_SERVICE = "promotedService";
    /**
     * Constant for model key: reference-name.
     */
    public static final String REFERENCE_NAME = "reference-name";
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
     * Constant for model key: state.
     */
    public static final String STATE = "state";
    /**
     * Constant for model key: throttling.
     */
    public static final String THROTTLING = "throttling";
    /**
     * Constant for model key: to.
     */
    public static final String TO = "to";
    /**
     * Constant for model key: transformations.
     */
    public static final String TRANSFORMERS = "transformers";
    /**
     * Constant for model key: artifacts.
     */
    public static final String ARTIFACTS = "artifacts";
    /**
     * Constant for model key: url.
     */
    public static final String URL = "url";
    /**
     * Constant for model key: validators.
     */
    public static final String VALIDATORS = "validators";
    
    /**
     * Constant for model key: successCount.
     */
    public static final String SUCCESS_COUNT = "successCount";

    /**
     * Constant for model key: faultCount.
     */
    public static final String FAULT_COUNT = "faultCount";
    
    /**
     * Constant for model key: totalCount.
     */
    public static final String TOTAL_COUNT = "totalCount";
    
    /**
     * Constant for model key: averageTime.
     */
    public static final String AVERAGE_TIME = "averageTime";
    
    /**
     * Constant for model key: minTime.
     */
    public static final String MIN_TIME = "minTime";
    
    /**
     * Constant for model key: maxTime.
     */
    public static final String MAX_TIME = "maxTime";
    
    /**
     * Constant for model key: totalTime.
     */
    public static final String TOTAL_TIME = "totalTime";

    /**
     * Constant for model key: enabled.
     */
    public static final String ENABLED = "enabled";
    /**
     * Constant for model key: maxRequests.
     */
    public static final String MAX_REQUESTS = "maxRequests";
    /**
     * Constant for model key: timePeriod.
     */
    public static final String TIME_PERIOD = "timePeriod";

    private SwitchYardModelConstants() {
    }

}
