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

package org.switchyard.metadata;

/**
 * Represents a component which registers a service and/or reference within a
 * service domain.
 */
public interface Registrant {

    /**
     * Indicates whether the registrant is a binding or implementation.
     * @return true if binding, false otherwise
     */
    boolean isBinding();
    
    /**
     * Indicates whether the registrant is a binding or implementation.
     * @return true if implementation, false otherwise
     */
    boolean isImplementation();
    
    /**
     * Returns a config representation based on the type of registrant.  
     * @param <T> Implementations will return ComponentImplementationModel, while 
     * bindings will return BindingModel.
     * @return type-specific configuration model
     */
    <T> T getConfig();
}
