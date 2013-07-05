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
package org.switchyard.security.credential;

import java.security.Principal;

/**
 * PrincipalCredential.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class PrincipalCredential implements Credential {
    
    private final Principal _principal;
    private final boolean _trusted;

    /**
     * Constructs an untrusted PrincipalCredential with the specified Principal.
     * @param principal the specified Principal
     */
    public PrincipalCredential(Principal principal) {
        this(principal, false);
    }

    /**
     * Constructs a PrincipalCredentail with the specified principal and trusted flag.
     * @param principal the specified Principal
     * @param trusted the trusted flag
     */
    public PrincipalCredential(Principal principal, boolean trusted) {
        _principal = principal;
        _trusted = trusted;
    }

    /**
     * Gets the Principal.
     * @return the Principal
     */
    public Principal getPrincipal() {
        return _principal;
    }

    /**
     * Gets the trusted flag.
     * @return the trusted flag
     */
    public boolean isTrusted() {
        return _trusted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "PrincipalCredential [principal=" + _principal + ", trusted=" + _trusted + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_principal == null) ? 0 : _principal.hashCode());
        result = prime * result + (_trusted ? 1231 : 1237);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PrincipalCredential other = (PrincipalCredential)obj;
        if (_principal == null) {
            if (other._principal != null) {
                return false;
            }
        } else if (!_principal.equals(other._principal)) {
            return false;
        }
        if (_trusted != other._trusted) {
            return false;
        }
        return true;
    }

}
