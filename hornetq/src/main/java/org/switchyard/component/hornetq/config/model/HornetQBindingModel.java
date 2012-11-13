/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.hornetq.config.model;

import org.switchyard.config.model.composite.BindingModel;

/**
 * A binding definition for binding HornetQ to SwitchYard.
 * 
 * @author Daniel Bevenius
 *
 */
public interface HornetQBindingModel extends BindingModel {
    
    /**
     * Retrieves the {@link HornetQConfigModel} for this binding.
     * 
     * @return {@link HornetQConfigModel} the HornetQ configuration model for this binding.
     */
    HornetQConfigModel getHornetQConfig();
    
    /**
     * Sets the {@link HornetQConfigModel} that this binding should use.
     * 
     * @param config the {@link HornetQConfigModel}.
     * @return {@link HornetQBindingModel} to support method chaining.
     */
    HornetQBindingModel setHornetQConfig(HornetQConfigModel config);
}
