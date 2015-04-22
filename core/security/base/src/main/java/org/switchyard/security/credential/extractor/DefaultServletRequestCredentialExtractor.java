/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.switchyard.security.credential.ConfidentialityCredential;
import org.switchyard.security.credential.Credential;
import org.switchyard.security.credential.PrincipalCredential;
import org.switchyard.security.principal.UserPrincipal;

/**
 * DefaultServletRequestCredentialExtractor.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class DefaultServletRequestCredentialExtractor implements ServletRequestCredentialExtractor {

    /**
     * Constructs a new DefaultServletRequestCredentialExtractor.
     */
    public DefaultServletRequestCredentialExtractor() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Credential> extract(ServletRequest source) {
        Set<Credential> credentials = new HashSet<Credential>();
        if (source != null) {
            if (source.isSecure()) {
                credentials.add(new ConfidentialityCredential(true));
            }
            if (source instanceof HttpServletRequest) {
                HttpServletRequest request = (HttpServletRequest)source;
                Principal userPrincipal = request.getUserPrincipal();
                if (userPrincipal != null) {
                    credentials.add(new PrincipalCredential(userPrincipal, true));
                }
                String remoteUser = request.getRemoteUser();
                if (remoteUser != null) {
                    credentials.add(new PrincipalCredential(new UserPrincipal(remoteUser), true));
                }
                String charsetName = source.getCharacterEncoding();
                AuthorizationHeaderCredentialExtractor ahce;
                if (charsetName != null) {
                    ahce = new AuthorizationHeaderCredentialExtractor(charsetName);
                } else {
                    ahce = new AuthorizationHeaderCredentialExtractor();
                }
                credentials.addAll(ahce.extract(request.getHeader("Authorization")));
            }
        }
        return credentials;
    }

}
