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

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.switchyard.common.codec.Base64;
import org.switchyard.common.lang.Strings;
import org.switchyard.security.BaseSecurityLogger;
import org.switchyard.security.credential.Credential;
import org.switchyard.security.credential.NameCredential;
import org.switchyard.security.credential.PasswordCredential;

/**
 * AuthorizationHeaderCredentialExtractor.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class AuthorizationHeaderCredentialExtractor implements CredentialExtractor<String> {

    private final Charset _charset;

    /**
     * Constructs a new AuthorizationHeaderCredentialsExtractor, with the platform-dependent charset.
     */
    public AuthorizationHeaderCredentialExtractor() {
        _charset = Charset.defaultCharset();
    }

    /**
     * Constructs a new AuthorizationHeaderCredentialsExtractor, with the specified charset name.
     * @param charsetName the specified charset name
     */
    public AuthorizationHeaderCredentialExtractor(String charsetName) {
        if (charsetName != null) {
            Charset charset;
            try {
                charset = Charset.forName(charsetName);
            } catch (Throwable t) {
                BaseSecurityLogger.ROOT_LOGGER.charSetNameIllegal(charsetName);
                charset = Charset.defaultCharset();
            }
            _charset = charset;
        } else {
            BaseSecurityLogger.ROOT_LOGGER.charSetNull();
            _charset = Charset.defaultCharset();
        }
    }

    /**
     * Constructs a new AuthorizationHeaderCredentialsExtractor, with the specified charset.
     * @param charset the specified charset
     */
    public AuthorizationHeaderCredentialExtractor(Charset charset) {
        if (charset != null) {
            _charset = charset;
        } else {
            BaseSecurityLogger.ROOT_LOGGER.charSetNull();
            _charset = Charset.defaultCharset();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Credential> extract(String source) {
        Set<Credential> credentials = new HashSet<Credential>();
        if (source != null) {
            if (source.startsWith("Basic ")) {
                String encoded = source.substring(6, source.length());
                String decoded = Base64.decodeToString(encoded, _charset);
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
                // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                // https://issues.jboss.org/browse/SWITCHYARD-1082
                // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
                // TODO: complete per SWITCHYARD-1082
            }
        }
        return credentials;
    }

}
