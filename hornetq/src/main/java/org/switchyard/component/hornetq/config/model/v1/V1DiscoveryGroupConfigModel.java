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

import javax.xml.namespace.QName;

import org.switchyard.component.hornetq.config.model.HornetQConstants;
import org.switchyard.component.hornetq.config.model.HornetQDiscoveryGroupConfigModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * Version 1.0 implementation of {@link HornetQDiscoveryGroupConfigModel}.
 * 
 * @author Daniel Bevenius
 *
 */
public class V1DiscoveryGroupConfigModel extends BaseModel implements HornetQDiscoveryGroupConfigModel {
   
    /**
     * Constructs a HornetQDiscoveryGroupConfigModel using the default {@link HornetQConstants#DEFAULT_NAMESPACE}.
     */
    public V1DiscoveryGroupConfigModel() {
        super(new QName(HornetQConstants.DEFAULT_NAMESPACE, DISCOVERY_GROUP));
        setModelChildrenOrder(
                LOCAL_BIND_ADDRESS,
                GROUP_ADDRESS, 
                GROUP_PORT, 
                REFRESH_TIMEOUT, 
                INITIAL_WAIT_TIMEOUT);
    }

    /**
     * Constructs a HornetQDiscoveryGroupConfigModel using the passed-in Configuration and 
     * Descriptor.
     * 
     * @param config the SwitchYard configuration.
     * @param desc the {@link Descriptor}.
     */
    public V1DiscoveryGroupConfigModel(final Configuration config, final Descriptor desc) {
        super(config, desc);
    }

    @Override
    public HornetQDiscoveryGroupConfigModel setLocalBindAddress(final String address) {
        setConfigValue(address, LOCAL_BIND_ADDRESS);
        return this;
    }

    @Override
    public String getLocalBindAddress() {
        return getConfigValue(LOCAL_BIND_ADDRESS);
    }

    @Override
    public HornetQDiscoveryGroupConfigModel setGroupAddress(final String address) {
        setConfigValue(address, GROUP_ADDRESS);
        return this;
    }

    @Override
    public String getGroupAddress() {
        return getConfigValue(GROUP_ADDRESS);
    }

    @Override
    public HornetQDiscoveryGroupConfigModel setGroupPort(final Integer port) {
        setConfigValue(String.valueOf(port), GROUP_PORT);
        return this;
    }

    @Override
    public Integer getGroupPort() {
        return getIntegerConfigValue(GROUP_PORT);
    }

    @Override
    public HornetQDiscoveryGroupConfigModel setRefreshTimeout(Long timeout) {
        setConfigValue(String.valueOf(timeout), REFRESH_TIMEOUT);
        return this;
    }

    @Override
    public Long getRefreshTimeout() {
        return getLongConfigValue(REFRESH_TIMEOUT);
    }
    
    @Override
    public HornetQDiscoveryGroupConfigModel setInitialWaitTimeout(Long timeout) {
        setConfigValue(String.valueOf(timeout), INITIAL_WAIT_TIMEOUT);
        return this;
    }

    @Override
    public Long getInitialWaitTimeout() {
        return getLongConfigValue(INITIAL_WAIT_TIMEOUT);
    }
    
    private void setConfigValue(final String value, String propertyName) {
        final Configuration config = getModelConfiguration().getFirstChild(propertyName);
        if (config != null) {
            config.setValue(value);
        } else {
            V1NameValueModel model = new V1NameValueModel(propertyName);
            model.setValue(value);
            setChildModel(model);
        }
    }
    
    private String getConfigValue(final String propertyName) {
        final Configuration config = getModelConfiguration().getFirstChild(propertyName);
        return config != null ? config.getValue() : null;
    }
    
    private Integer getIntegerConfigValue(final String propertyName) {
        final Configuration config = getModelConfiguration().getFirstChild(propertyName);
        return config != null ? Integer.valueOf(config.getValue()) : null;
    }
    
    private Long getLongConfigValue(final String propertyName) {
        final Configuration config = getModelConfiguration().getFirstChild(propertyName);
        return config != null ? Long.valueOf(config.getValue()) : null;
    }

}
