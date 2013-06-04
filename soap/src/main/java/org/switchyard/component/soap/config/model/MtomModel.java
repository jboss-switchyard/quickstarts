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
package org.switchyard.component.soap.config.model;

import org.switchyard.config.model.Model;

/**
 * A Mtom Model.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public interface MtomModel extends Model {

    /**
     * Get if MTOM enabled.
     * @return true if enabled, false otherwise
     */
    public Boolean isEnabled();

    /**
     * Enable or disable MTOM feature.
     * @param enabled true to enable, false to diable
     * @return this MtomModel (useful for chaining)
     */
    public MtomModel setEnabled(Boolean enabled);

    /**
     * Gets the threshold value.
     * @return the configName
     */
    public Integer getThreshold();

    /**
     * Sets the threshold value.
     * @param threshold the threshold value to set
     * @return this MtomModel (useful for chaining)
     */
    public MtomModel setThreshold(Integer threshold);

    /**
     * Get if XOP expand enabled.
     * @return true if XOP expand enabled, false otherwise
     */
    public Boolean isXopExpand();

    /**
     * Enable or disable XOP expand feature.
     * @param enabled true to enable, false to diable
     * @return this MtomModel (useful for chaining)
     */
    public MtomModel setXopExpand(Boolean enabled);

}
