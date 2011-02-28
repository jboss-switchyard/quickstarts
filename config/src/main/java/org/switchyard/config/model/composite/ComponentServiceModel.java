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

package org.switchyard.config.model.composite;

import org.switchyard.config.model.NamedModel;

/**
 * The "component/service" model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface ComponentServiceModel extends NamedModel {

    /** The "service"  name. */
    public static final String SERVICE = CompositeServiceModel.SERVICE;

    /**
     * Gets the parent component model.
     * @return the parent component model
     */
    public ComponentModel getComponent();

    /**
     * Gets the child component service interface model.
     * @return the child component service interface model
     */
    public ComponentServiceInterfaceModel getInterface();

    /**
     * Sets the child component service interface model.
     * @param interfaze child component service interface model
     * @return this ComponentServiceModel (useful for chaining)
     */
    public ComponentServiceModel setInterface(ComponentServiceInterfaceModel interfaze);

}
