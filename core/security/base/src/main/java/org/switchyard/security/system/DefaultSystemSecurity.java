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
package org.switchyard.security.system;

import java.util.UUID;

import org.switchyard.security.crypto.PrivateCrypto;
import org.switchyard.security.crypto.PublicCrypto;

/**
 * DefaultSystemSecurity.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class DefaultSystemSecurity implements SystemSecurity {

    private static final String FORMAT = DefaultSystemSecurity.class.getSimpleName() + "@%s[uuid=%s, securityContextTimeoutMillis=%s, privateCrypto=%s, publicCrypto=%s]";

    private final UUID _uuid;
    private Long _securityContextTimeoutMillis;
    private PrivateCrypto _privateCrypto;
    private PublicCrypto _publicCrypto;

    /**
     * Constructs a new DefaultSystemSecurity with null properties.
     */
    public DefaultSystemSecurity() {
        this(null, null, null);
    }

    /**
     * Constructs a new DefaultSystemSecurity with specified properties.
     * @param securityContextTimeoutMillis the security context timeout in milliseconds
     * @param privateCrypto the private crypto
     * @param publicCrypto the public crypto
     */
    public DefaultSystemSecurity(Long securityContextTimeoutMillis, PrivateCrypto privateCrypto, PublicCrypto publicCrypto) {
        _uuid = UUID.randomUUID();
        _securityContextTimeoutMillis = securityContextTimeoutMillis;
        _privateCrypto = privateCrypto;
        _publicCrypto = publicCrypto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID getUUID() {
        return _uuid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getSecurityContextTimeoutMillis() {
        return _securityContextTimeoutMillis;
    }

    /**
     * Sets the security context timeout in milliseconds.
     * @param securityContextTimeoutMillis the security context timeout in milliseconds
     */
    public void setSecurityContextTimeoutMillis(Long securityContextTimeoutMillis) {
        _securityContextTimeoutMillis = securityContextTimeoutMillis;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrivateCrypto getPrivateCrypto() {
        return _privateCrypto;
    }

    /**
     * Sets the private crypto.
     * @param privateCrypto the private crypto
     */
    public void setPrivateCrypto(PrivateCrypto privateCrypto) {
        _privateCrypto = privateCrypto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PublicCrypto getPublicCrypto() {
        return _publicCrypto;
    }

    /**
     * Sets the public crypto.
     * @param publicCrypto the public crypto
     */
    public void setPublicCrypto(PublicCrypto publicCrypto) {
        _publicCrypto = publicCrypto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format(FORMAT, System.identityHashCode(this), _uuid, _securityContextTimeoutMillis, _privateCrypto, _publicCrypto);
    }

    /**
     * An immutable DefaultSystemSecurity.
     */
    static final class ImmutableDefaultSystemSecurity extends DefaultSystemSecurity {
        @Override
        public void setSecurityContextTimeoutMillis(Long securityContextTimeoutMillis) {
            throw new UnsupportedOperationException();
        }
        @Override
        public void setPrivateCrypto(PrivateCrypto privateCrypto) {
            throw new UnsupportedOperationException();
        }
        @Override
        public void setPublicCrypto(PublicCrypto publicCrypto) {
            throw new UnsupportedOperationException();
        }
    }

}
