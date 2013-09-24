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
package org.switchyard.policy;

/**
 * Supported types for <a href="http://docs.oasis-open.org/opencsa/sca-policy/sca-policy-1.1-spec-csprd03.html#_Toc311121482">Security Policy</a>.
 * 
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public enum SecurityPolicy implements Policy {

    /**
     * Ensure that the client is authorized to use the service.
     */
    AUTHORIZATION("authorization") {
        @Override
        public boolean supports(PolicyType type) {
            // "The SCA Security specification defines the authorization intent to specify implementation policy."
            // NOTE: We also still support interaction policy for backwards-compatibility with SwitchYard 1.0.
            return type == PolicyType.IMPLEMENTATION || type == PolicyType.INTERACTION;
        }
    },

    /**
     * Ensure that the client is authenticated by the server.
     */
    CLIENT_AUTHENTICATION("clientAuthentication") {
        @Override
        public boolean supports(PolicyType type) {
            // "The SCA security specification defines the following intents to specify interaction policy: ..., clientAuthentication, ..."
            return type == PolicyType.INTERACTION;
        }
    },

    /**
     * Ensure that only authorized entities can view the contents of a message.
     */
    CONFIDENTIALITY("confidentiality") {
        @Override
        public boolean supports(PolicyType type) {
            // "The SCA security specification defines the following intents to specify interaction policy: ..., confidentiality, ..."
            return type == PolicyType.INTERACTION;
        }
    };

    private String _name;

    private SecurityPolicy(String name) {
        _name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCompatibleWith(Policy target) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Policy getPolicyDependency() {
        return null;
    }

}
