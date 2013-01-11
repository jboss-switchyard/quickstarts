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
