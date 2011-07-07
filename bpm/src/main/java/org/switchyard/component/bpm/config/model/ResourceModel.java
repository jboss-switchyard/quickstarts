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
package org.switchyard.component.bpm.config.model;

import org.switchyard.component.bpm.resource.Resource;
import org.switchyard.component.bpm.resource.ResourceType;
import org.switchyard.config.model.Model;

/**
 * A configuration model for Resources.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface ResourceModel extends Resource, Model {

    /**
     * The resource XML element.
     */
    public static final String RESOURCE = "resource";

    /**
     * Sets the location of the Resource.
     * @param location the location of the Resource.
     * @return this ResourceModel (useful for chaining)
     */
    public ResourceModel setLocation(String location);

    /**
     * Sets the type of the Resource.
     * @param type the type of Resource
     * @return this ResourceModel (useful for chaining)
     */
    public ResourceModel setType(ResourceType type);

}
