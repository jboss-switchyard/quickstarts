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

/**
 * ConfidentialityCredential.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class ConfidentialityCredential implements Credential {

    private static final long serialVersionUID = -7520434970909546852L;
    private static final String FORMAT = ConfidentialityCredential.class.getSimpleName() + "@%s[confidential=%s]";

    private final boolean _confidential;

    /**
     * Constructs a ConfidentialityCredential with the specified confidential flag.
     * @param confidential the specified confidential flag
     */
    public ConfidentialityCredential(boolean confidential) {
        _confidential = confidential;
    }

    /**
     * Gets the confidential flag.
     * @return the confidential flag
     */
    public boolean isConfidential() {
        return _confidential;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format(FORMAT, System.identityHashCode(this), _confidential);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (_confidential ? 1231 : 1237);
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
        ConfidentialityCredential other = (ConfidentialityCredential)obj;
        if (_confidential != other._confidential) {
            return false;
        }
        return true;
    }

}
