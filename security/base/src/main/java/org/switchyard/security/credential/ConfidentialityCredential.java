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

/**
 * ConfidentialityCredential.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class ConfidentialityCredential implements Credential {

    private static final long serialVersionUID = 8954536627394490993L;

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
        return "ConfidentialityCredential [confidential=" + _confidential + "]";
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
