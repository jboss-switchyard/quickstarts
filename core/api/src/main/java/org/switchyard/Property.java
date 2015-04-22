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

package org.switchyard;

import java.util.Set;

/**
 * Represents a context property consisting of a name, scope, and value.
 */
public interface Property {
    
    /**
     * The scope of the property.
     * @return property scope
     */
    Scope getScope();

    /**
     * The name of the property.
     * @return property name
     */
    String getName();

    /**
     * The value of the property.
     * @return property value
     */
    Object getValue();

    /**
     * The labels of the property.
     * @return the labels
     */
    Set<String> getLabels();

    /**
     * Adds labels to the property.
     * @param labels the labels to add
     * @return this property (useful for chaining)
     */
    Property addLabels(String... labels);
    
    /**
     * Adds a set of labels to the property.
     * @param labels the labels to add
     * @return this property (useful for chaining)
     */
    Property addLabels(Set<String> labels);

    /**
     * Removes labels from the property.
     * @param labels the labels to remove
     * @return this property (useful for chaining)
     */
    Property removeLabels(String... labels);

    /**
     * If the property has the specified label.
     * @param label the specified label
     * @return this property (useful for chaining)
     */
    boolean hasLabel(String label);

}
