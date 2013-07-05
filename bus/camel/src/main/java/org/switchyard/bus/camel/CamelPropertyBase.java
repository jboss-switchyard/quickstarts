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
package org.switchyard.bus.camel;

import java.util.Arrays;
import java.util.Collections;
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
        Set<String> labels = getLabelsBag().get(getName());
        if (labels == null) {
            labels = Collections.emptySet();
        }
        return labels;
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
    public Property addLabels(Set<String> labels) {
        if (!getLabelsBag().containsKey(getName())) {
            getLabelsBag().put(getName(), new HashSet<String>());
        }
        getLabelsBag().get(getName()).addAll(labels);
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
