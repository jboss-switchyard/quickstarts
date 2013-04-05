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
package org.switchyard.bus.camel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.switchyard.Property;

/**
 * Base Property class for camel properties - both Message and Exchange scope.
 */
public abstract class CamelPropertyBase implements Property {

    @Override
    public Set<String> getLabels() {
        return getLabelsBag().get(getName());
    }

    @Override
    public Property addLabels(String... labels) {
        if (!getLabelsBag().containsKey(getName())) {
            getLabelsBag().put(getName(), new HashSet<String>());
        }
        getLabelsBag().get(getName()).addAll(Arrays.asList(labels));
        return this;
    }

    @Override
    public Property removeLabels(String... labels) {
        if (getLabelsBag().containsKey(getName())) {
            getLabelsBag().get(getName()).removeAll(Arrays.asList(labels));
        }
        return this;
    }

    @Override
    public boolean hasLabel(String label) {
        return getLabelsBag().containsKey(getName()) ? getLabelsBag().get(getName()).contains(label) : false;
    }

    /**
     * Retrieves bag which allows to store map between property names and property labels.
     * 
     * @return Map to keep property labels.
     */
    protected abstract Map<String, Set<String>> getLabelsBag();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (getName() == null ? 0 : getName().hashCode());
        result = prime * result + (getScope() == null ? 0 : getScope().hashCode());
        result = prime * result + (getValue() == null ? 0 : getValue().hashCode());
        return result;
    }

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
        Property other = (Property) obj;
        if (getName() == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!getName().equals(other.getName())) {
            return false;
        }
        if (getScope() != other.getScope()) {
            return false;
        }
        if (getValue() == null) {
            if (other.getValue() != null) {
                return false;
            }
        } else if (!getValue().equals(other.getValue())) {
            return false;
        }
        return true;
    }

}
