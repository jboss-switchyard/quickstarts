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
package org.switchyard.rhq.plugin.operations;

import static org.switchyard.rhq.plugin.SwitchYardConstants.ADDRESS_SWITCHYARD;
import static org.switchyard.rhq.plugin.SwitchYardConstants.DMR_UPDATE_THROTTLING;
import static org.switchyard.rhq.plugin.SwitchYardConstants.PARAM_APPLICATION_NAME;
import static org.switchyard.rhq.plugin.SwitchYardConstants.PARAM_SERVICE_NAME;
import static org.switchyard.rhq.plugin.SwitchYardConstants.PARAM_THROTTLING;

import org.rhq.modules.plugins.jbossas7.json.Operation;
import org.switchyard.rhq.plugin.model.Throttling;

/**
 * Reset Metrics
 */
public class UpdateThrottling extends Operation {
    public UpdateThrottling(final String applicationName, final String compositeName, final Boolean enabled, final Integer maxRequests) {
        super(DMR_UPDATE_THROTTLING, ADDRESS_SWITCHYARD);
        addAdditionalProperty(PARAM_APPLICATION_NAME, applicationName);
        addAdditionalProperty(PARAM_SERVICE_NAME, compositeName);
        addAdditionalProperty(PARAM_THROTTLING, new Throttling(enabled, maxRequests, null));
    }
}
