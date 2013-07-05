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
package org.switchyard.security.credential.extractor;

import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

import org.switchyard.security.credential.CertificateCredential;
import org.switchyard.security.credential.ConfidentialityCredential;
import org.switchyard.security.credential.Credential;
import org.switchyard.security.credential.PrincipalCredential;

/**
 * CredentialExtractor which extracts {@link Credential}s from a given {@link SSLSession}.
 */
public class SSLSessionCredentialExtractor implements CredentialExtractor<SSLSession> {

    /**
     * Constructs a new SSLSessionCredentialExtractor.
     */
    public SSLSessionCredentialExtractor() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Credential> extract(SSLSession source) {
        Set<Credential> credentials = new HashSet<Credential>();
        try {
            credentials.add(new ConfidentialityCredential(source.isValid()));
            credentials.add(new PrincipalCredential(source.getPeerPrincipal()));
            credentials.add(new CertificateCredential(source.getPeerCertificates()[0]));
        } catch (SSLPeerUnverifiedException e) {
            throw new RuntimeException("Unable to extract Credentials from SSLSession: " + e.getMessage(), e);
        }
        return credentials;
    }

}
