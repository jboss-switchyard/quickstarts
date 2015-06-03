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
package org.switchyard.security.jboss.credential.extractor;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import org.jboss.com.sun.net.httpserver.HttpExchange;
import org.switchyard.common.lang.Strings;
import org.switchyard.security.credential.ConfidentialityCredential;
import org.switchyard.security.credential.Credential;
import org.switchyard.security.credential.PrincipalCredential;
import org.switchyard.security.credential.extractor.AuthorizationHeaderCredentialExtractor;
import org.switchyard.security.credential.extractor.CredentialExtractor;

/**
 * HttpExchangeCredentialExtractor.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class HttpExchangeCredentialExtractor implements CredentialExtractor<HttpExchange> {

    /**
     * Constructs a new HttpExchangeCredentialsExtractor.
     */
    public HttpExchangeCredentialExtractor() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Credential> extract(HttpExchange source) {
        Set<Credential> credentials = new HashSet<Credential>();
        if (source != null) {
            String protocol = source.getProtocol();
            if (protocol != null) {
                credentials.add(new ConfidentialityCredential(protocol.toLowerCase().startsWith("https")));
            }
            Principal principal = source.getPrincipal();
            if (principal != null) {
                credentials.add(new PrincipalCredential(principal, true));
            }
            // TODO: Should we go after this?
            /*
            String remoteUser = source.getRequestHeaders().getFirst("REMOTE_USER");
            if (remoteUser != null) {
                credentials.add(new PrincipalCredential(new User(remoteUser), false)); // true?
            }
            */
            String charsetName = null;
            String contentType = source.getRequestHeaders().getFirst("Content-Type");
            if (contentType != null) {
                int pos = contentType.lastIndexOf("charset=");
                if (pos > -1) {
                    charsetName = Strings.trimToNull(contentType.substring(pos+8, contentType.length()));
                }
            }
            AuthorizationHeaderCredentialExtractor ahce;
            if (charsetName != null) {
                ahce = new AuthorizationHeaderCredentialExtractor(charsetName);
            } else {
                ahce = new AuthorizationHeaderCredentialExtractor();
            }
            credentials.addAll(ahce.extract(source.getRequestHeaders().getFirst("Authorization")));
        }
        return credentials;
    }

}
