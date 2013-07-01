/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
