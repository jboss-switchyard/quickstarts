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
package org.switchyard.component.hornetq.config.model.v1;

import org.switchyard.component.common.selector.config.model.v1.V1BindingModel;
import org.switchyard.component.hornetq.config.model.HornetQBindingModel;
import org.switchyard.component.hornetq.config.model.HornetQConfigModel;
import org.switchyard.component.hornetq.config.model.HornetQConstants;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * 
 * @author Daniel Bevenius
 *
 */
public class V1HornetQBindingModel extends V1BindingModel implements HornetQBindingModel {
    
    private HornetQConfigModel _configModel;

    /**
     * Constructs a HornetQBindingModel using the default {@link HornetQConstants#DEFAULT_NAMESPACE}.
     */
    public V1HornetQBindingModel() {
        super(HornetQConstants.TYPE, HornetQConstants.DEFAULT_NAMESPACE);
        setModelChildrenOrder(HornetQConfigModel.CONFIG);
    }

    /**
     * Constructs a HornetQBindingModel using the passed-in Configuration and 
     * Descriptor.
     * 
     * @param config the SwitchYard configuration.
     * @param desc the {@link Descriptor}.
     */
    public V1HornetQBindingModel(final Configuration config, final Descriptor desc) {
        super(config, desc);
    }
    
    @Override
    public HornetQConfigModel getHornetQConfig() {
        if (_configModel == null) {
            _configModel = (HornetQConfigModel) getFirstChildModel(HornetQConfigModel.CONFIG);
        }
        return _configModel;
    }
    
    @Override
    public HornetQBindingModel setHornetQConfig(final HornetQConfigModel config) {
        setChildModel(config);
        _configModel = config;
        return this;
    }

}
