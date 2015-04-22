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
package org.switchyard.rhq.plugin;

import org.rhq.modules.plugins.jbossas7.json.Address;

/**
 * SwitchYard Constants
 */
public class SwitchYardConstants {
    /**
     * The SwitchYard subsystem address.
     */
    public static final Address ADDRESS_SWITCHYARD = new Address("subsystem=switchyard");

    /**
     * The invocation outcome flag.
     */
    public static final String INVOCATION_OUTCOME = "outcome";
    /**
     * The invocation result.
     */
    public static final String INVOCATION_RESULT = "result";
    /**
     * The failure description value.
     */
    public static final String INVOCATION_FAILURE_DESCRIPTION = "failure-description";
    /**
     * The successful outcome value.
     */
    public static final String OUTCOME_SUCCESS = "success";


    /**
     * The get-version operation
     */
    public static final String DMR_GET_VERSION = "get-version";
    /**
     * The read-application operation
     */
    public static final String DMR_READ_APPLICATION = "read-application";
    /**
     * The read-service operation
     */
    public static final String DMR_READ_SERVICE = "read-service";
    /**
     * The read-reference operation
     */
    public static final String DMR_READ_REFERENCE = "read-reference";
    /**
     * The show-metrics operation
     */
    public static final String DMR_SHOW_METRICS = "show-metrics";
    /**
     * The reset-metrics operation
     */
    public static final String DMR_RESET_METRICS = "reset-metrics";
    /**
     * The start-gateway operation
     */
    public static final String DMR_START_GATEWAY = "start-gateway";
    /**
     * The stop-gateway operation
     */
    public static final String DMR_STOP_GATEWAY = "stop-gateway";
    /**
     * The update-throttling operation
     */
    public static final String DMR_UPDATE_THROTTLING = "update-throttling";


    /**
     * The name parameter
     */
    public static final String PARAM_NAME = "name";
    /**
     * The type parameter
     */
    public static final String PARAM_TYPE = "type";
    /**
     * The application-name parameter
     */
    public static final String PARAM_APPLICATION_NAME = "application-name";
    /**
     * The service-name parameter
     */
    public static final String PARAM_SERVICE_NAME = "service-name";
    /**
     * The reference-name parameter
     */
    public static final String PARAM_REFERENCE_NAME = "reference-name";
    /**
     * The throttling parameter
     */
    public static final String PARAM_THROTTLING = "throttling";


    /**
     * The reset operation
     */
    public static final String OPERATION_RESET = "reset";
    /**
     * The start operation
     */
    public static final String OPERATION_START = "start";
    /**
     * The stop operation
     */
    public static final String OPERATION_STOP = "stop";
    /**
     * The setThrottling operation
     */
    public static final Object OPERATION_SET_THROTTLING = "setThrottling";
    /**
     * The enabled parameter
     */
    public static final String OPERATION_PARAMETER_ENABLED = "enabled";
    /**
     * The maxRequests parameter
     */
    public static final String OPERATION_PARAMETER_MAX_REQUESTS = "maxRequests";


    /**
     * The SwitchYard component key.
     */
    public static final String KEY_SWITCH_YARD = "SwitchYardSubsystem";
    /**
     * The SwitchYard component description.
     */
    public static final String DESCRIPTION_SWITCH_YARD = "Container for SwitchYard Applications";
    /**
     * The SwitchYard component name.
     */
    public static final String NAME_SWITCH_YARD = "SwitchYard";
    
    
    /**
     * The success count metric
     */
    public static final String METRIC_SUCCESS_COUNT = "SuccessCount";
    /**
     * The fault count metric
     */
    public static final String METRIC_FAULT_COUNT = "FaultCount";
    /**
     * The total count metric
     */
    public static final String METRIC_TOTAL_COUNT = "TotalCount";
    /**
     * The total processing time metric
     */
    public static final String METRIC_TOTAL_PROCESSING_TIME = "TotalProcessingTime";
    /**
     * The average processing time metric
     */
    public static final String METRIC_AVERAGE_PROCESSING_TIME = "AverageProcessingTime";
    /**
     * The minimum processing time metric
     */
    public static final String METRIC_MIN_PROCESSING_TIME = "MinProcessingTime";
    /**
     * The maximum processing time metric
     */
    public static final String METRIC_MAX_PROCESSING_TIME = "MaxProcessingTime";
    /**
     * The throttling enabled metric
     */
    public static final String METRIC_THROTTLING_ENABLED = "throttlingEnabled";
    /**
     * The throttling time period metric
     */
    public static final String METRIC_TIME_PERIOD = "TimePeriod";
    /**
     * The throttling max requests metric
     */
    public static final String METRIC_MAX_REQUESTS = "MaxRequests";
    /**
     * The state metric
     */
    public static final String METRIC_STATE = "State";
}
