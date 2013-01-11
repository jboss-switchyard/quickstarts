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

import java.util.Arrays;

/**
 * PasswordCredential.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class PasswordCredential implements Credential {

    private final char[] _password;

    /**
     * Constructs a PasswordCredential with the specified password.
     * @param password the specified password
     */
    public PasswordCredential(String password) {
        _password = password != null ? password.toCharArray() : null;
    }

    /**
     * Constructs a PasswordCredential with the specified password.
     * @param password the specified password
     */
    public PasswordCredential(char[] password) {
        _password = password;
    }

    /**
     * Gets the password.
     * @return the password
     */
    public char[] getPassword() {
        return _password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder masked = new StringBuilder();
        if (_password != null) {
            for (int i=0; i < _password.length; i++) {
                masked.append('*');
            }
        } else {
            masked.append("null");
        }
        return "PasswordCredentialImpl [password=" + masked + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(_password);
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
        PasswordCredential other = (PasswordCredential)obj;
        if (!Arrays.equals(_password, other._password)) {
            return false;
        }
        return true;
    }

}
