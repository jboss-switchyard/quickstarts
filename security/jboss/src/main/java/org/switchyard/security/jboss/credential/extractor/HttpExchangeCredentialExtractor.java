/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
