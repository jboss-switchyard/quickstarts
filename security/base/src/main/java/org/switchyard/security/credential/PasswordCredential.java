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
