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
package org.switchyard.component.camel.switchyard.test;

import java.util.HashSet;
import java.util.Set;

import org.apache.camel.Message;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.common.composer.SecurityBindingData;
import org.switchyard.security.credential.ConfidentialityCredential;
import org.switchyard.security.credential.Credential;

/**
 * Secure binding data for test endpoint.
 */
public class SecureBindingData extends CamelBindingData implements SecurityBindingData {

    public SecureBindingData(Message message) {
        super(message);
    }

    @Override
    public Set<Credential> extractCredentials() {
        Set<Credential> credentials = new HashSet<Credential>();
        credentials.add(new ConfidentialityCredential(true));
        return credentials;
    }

}
