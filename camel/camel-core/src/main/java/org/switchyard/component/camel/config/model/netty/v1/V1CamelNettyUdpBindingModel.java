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
package org.switchyard.component.camel.config.model.netty.v1;

import org.switchyard.component.camel.config.model.netty.CamelNettyUdpBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of netty udp binding.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelNettyUdpBindingModel extends V1CamelNettyBindingModel
    implements CamelNettyUdpBindingModel {

    /**
     * Protocol scheme.
     */
    public static final String UDP = "udp";

    /**
     * Configuration element name.
     */
    public static final String NETTY_UDP = NETTY + "-" + UDP;

    private static final String BROADCAST = "broadcast";

    /**
     * Create a new CamelDirectBindingModel.
     */
    public V1CamelNettyUdpBindingModel() {
        super(NETTY_UDP);
        setModelChildrenOrder(BROADCAST);
    }

    /**
     * Create a V1CamelNettyBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelNettyUdpBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    protected String getProtocol() {
        return UDP;
    }

    @Override
    public Boolean isBroadcast() {
        return getBooleanConfig(BROADCAST);
    }

    @Override
    public V1CamelNettyUdpBindingModel setBroadcast(Boolean broadcast) {
        return setConfig(BROADCAST, broadcast);
    }

}
