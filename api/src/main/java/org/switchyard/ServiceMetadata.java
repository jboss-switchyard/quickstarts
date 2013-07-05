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

package org.switchyard;

import java.util.List;

import org.switchyard.metadata.Registrant;
import org.switchyard.metadata.qos.Throttling;
import org.switchyard.policy.Policy;

/**
 * Contains runtime details on services and service references registered
 * in SwitchYard.  Instances of ServiceMetadata can be created and updated
 * using the ServiceMetadataBuilder class.
 */
public interface ServiceMetadata {

    /**
     * Gets the security.
     * @return the security
     */
    ServiceSecurity getSecurity();
    
    /**
     * Returns a list of required policies for this service reference.
     * @return list of required policy
     */
    List<Policy> getRequiredPolicies();

    /**
     * Returns a list of policies provided by this service reference.
     * @return list of provided policy
     */
    List<Policy> getProvidedPolicies();
    
    /**
     * Return the consumer metadata associated with this service.
     * @return consumer metadata
     */
    Registrant getRegistrant();
    
    /**
     * Return the throttling configuration associated with a service reference.  Throttling
     * config only applies to consumers via service references.
     * @return throttling config
     */
    Throttling getThrottling();
}
