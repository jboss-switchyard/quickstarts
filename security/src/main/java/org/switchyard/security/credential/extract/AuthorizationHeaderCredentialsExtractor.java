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
package org.switchyard.security.credential.extract;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.switchyard.common.codec.Base64;
import org.switchyard.common.lang.Strings;
import org.switchyard.security.credential.Credential;
import org.switchyard.security.credential.NameCredential;
import org.switchyard.security.credential.PasswordCredential;

/**
 * AuthorizationHeaderCredentialsExtractor.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class AuthorizationHeaderCredentialsExtractor implements CredentialsExtractor<String> {

    /**
     * Constructs a new AuthorizationHeaderCredentialsExtractor.
     */
    public AuthorizationHeaderCredentialsExtractor() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Credential> extractCredentials(String source) {
        Set<Credential> credentials = new HashSet<Credential>();
        if (source != null) {
            if (source.startsWith("Basic ")) {
                String encoded = source.substring(6, source.length());
                String decoded = Base64.decode(encoded);
                if (decoded.indexOf(':') != -1) {
                    String[] split = decoded.split(":", 2);
                    String name = split.length > 0 ? split[0] : null;
                    if (name != null) {
                        credentials.add(new NameCredential(name));
                    }
                    String password = split.length > 1 ? split[1] : null;
                    if (password != null) {
                        credentials.add(new PasswordCredential(password));
                    }
                }
            } else if (source.startsWith("Digest ")) {
                Map<String,String> map = new HashMap<String,String>();
                String everything = source.substring(6, source.length()).trim();
                List<String> list = Strings.splitTrimToNull(everything, ",\n");
                for (String pair : list) {
                    String[] split = pair.split("=", 2);
                    String key = split.length > 0 ? split[0] : null;
                    String value = split.length > 1 ? split[1] : null;
                    if (key != null && value != null) {
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            value = value.substring(1, value.length() - 1);
                        }
                        map.put(key, value);
                    }
                }
                String username = map.get("username");
                if (username != null) {
                    credentials.add(new NameCredential(username));
                }
                // TODO: password?
            }
        }
        return credentials;
    }

}
