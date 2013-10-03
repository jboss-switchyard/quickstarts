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
import static org.switchyard.rhq.plugin.SwitchYardConstants.DMR_READ_REFERENCE;
import static org.switchyard.rhq.plugin.SwitchYardConstants.PARAM_APPLICATION_NAME;
import static org.switchyard.rhq.plugin.SwitchYardConstants.PARAM_REFERENCE_NAME;

import org.rhq.modules.plugins.jbossas7.json.Operation;

/**
 * Read Application operation
 */
public class ReadReference extends Operation {
    public ReadReference(final String application, final String reference) {
        super(DMR_READ_REFERENCE, ADDRESS_SWITCHYARD);
        addAdditionalProperty(PARAM_APPLICATION_NAME, application);
        addAdditionalProperty(PARAM_REFERENCE_NAME, reference);
    }
}
