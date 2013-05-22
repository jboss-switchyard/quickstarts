/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.component.soap;

/**
 * Addressing booleans.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class Addressing {

    private Boolean _enabled = false;
    private Boolean _required = false;

    /**
     * Check if addressing feature is enabled.
     * 
     * @return true if addressing is enabled, false otherwise
     */
    public Boolean isEnabled() {
        return _enabled;
    }

    /**
     * Set if addressing is enabled.
     * 
     * @param enabled true if addressing is enabled, false otherwise
     */
    public void setEnabled(Boolean enabled) {
        _enabled = enabled;
    }

    /**
     * Check if addressing feature is required.
     * 
     * @return true if addressing is required, false otherwise
     */
    public Boolean isRequired() {
        return _required;
    }

    /**
     * Set if addressing is required.
     * 
     * @param required true if addressing is required, false otherwise
     */
    public void setRequired(Boolean required) {
        _required = required;
    }

    /**
     * Returns a String representation of this class.
     * 
     * @return the String representation
     */
    public String toString() {
        return "[Enabled:" + _enabled + ", Required:" +_required + "]";
    }
}
