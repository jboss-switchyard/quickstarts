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

package org.switchyard.metadata;

import java.util.Collections;
import java.util.List;

import org.switchyard.APIMessages;
import org.switchyard.ServiceMetadata;
import org.switchyard.ServiceSecurity;
import org.switchyard.metadata.qos.Throttling;
import org.switchyard.policy.Policy;

/**
 * Fluent builder for ServiceMetadata instances.  This class can be used
 * to create new ServiceMetadata objects or update existing instances of 
 * ServiceMetadata associated with a service or service reference.
 */
public final class ServiceMetadataBuilder {
    
    private ServiceMetadataImpl _metadata;
    
    private ServiceMetadataBuilder() {
        _metadata = new ServiceMetadataImpl();
    }
    
    private ServiceMetadataBuilder(ServiceMetadataImpl metadata) {
        _metadata = metadata;
    }

    /**
     * Specifies service security for service metadata.
     * @param security service security
     * @return this builder
     */
    public ServiceMetadataBuilder security(ServiceSecurity security) {
        _metadata._security = security;
        return this;
    }
    
    /**
     * Specifies required policies for service metadata.
     * @param requiredPolicies required policies
     * @return this builder
     */
    public ServiceMetadataBuilder requiredPolicies(List<Policy> requiredPolicies) {
        _metadata._requiredPolicies = requiredPolicies;
        return this;
    }
    
    /**
     * Specifies provided policies for service metadata.
     * @param providedPolicies provided policies
     * @return this builder
     */
    public ServiceMetadataBuilder providedPolicies(List<Policy> providedPolicies) {
        _metadata._providedPolicies = providedPolicies;
        return this;
    }
    
    /**
     * Specifies the registrant of a service or service reference associated with this metadata.
     * @param registrant the registrant of a service or reference
     * @return this builder
     */
    public ServiceMetadataBuilder registrant(Registrant registrant) {
        _metadata._registrant = registrant;
        return this;
    }
    
    /**
     * Specifies throttling for service references.  NOTE : this setting is ignored
     * for services at present.
     * @param throttling throttling setting
     * @return this builder
     */
    public ServiceMetadataBuilder throttling(Throttling throttling) {
        _metadata._throttling = throttling;
        return this;
    } 
    
    /**
     * Returns a configured ServiceMetadata instance.
     * @return ServiceMetadata
     */
    public ServiceMetadata build() {
        return _metadata;
    }
    
    /**
     * Creates a new ServiceMetadata instance.  This method is called first when building 
     * a new ServiceMetadata instance.
     * @return a reference to this builder to configure the ServiceMetadata
     */
    public static ServiceMetadataBuilder create() {
        return new ServiceMetadataBuilder();
    }
    
    /**
     * Used to update an existing instance of ServiceMetadata by chaining additional
     * calls after the call to update().
     * @param metadata the ServiceMetadata to update
     * @return a reference to this builder to configure the ServiceMetadata
     */
    public static ServiceMetadataBuilder update(ServiceMetadata metadata) {
        if (!ServiceMetadataImpl.class.isInstance(metadata)) {
            throw APIMessages.MESSAGES.unableUpdateMetadataType(metadata.getClass().toString());
        }
        return new ServiceMetadataBuilder((ServiceMetadataImpl)metadata);
    }

    /**
     * A copy operation which is compatible with the fluent builder API in case changes
     * are necessary.
     * @param metadata the ServiceMetadata to copy
     * @return a reference to this builder to configure the ServiceMetadata
     */
    public static ServiceMetadataBuilder createFrom(ServiceMetadata metadata) {
        return create()
            .security(metadata.getSecurity())
            .registrant(metadata.getRegistrant())
            .requiredPolicies(metadata.getRequiredPolicies())
            .providedPolicies(metadata.getProvidedPolicies());
    }

    private class ServiceMetadataImpl implements ServiceMetadata {
        
        private ServiceSecurity _security;
        private List<Policy> _requiredPolicies = Collections.emptyList();
        private List<Policy> _providedPolicies = Collections.emptyList();
        private Registrant _registrant;
        private Throttling _throttling;

        @Override
        public ServiceSecurity getSecurity() {
            return _security;
        }

        @Override
        public List<Policy> getRequiredPolicies() {
            return _requiredPolicies;
        }

        @Override
        public List<Policy> getProvidedPolicies() {
            return _providedPolicies;
        }

        @Override
        public Registrant getRegistrant() {
            return _registrant;
        }

        @Override
        public Throttling getThrottling() {
            return _throttling;
        }
        
    }
}
