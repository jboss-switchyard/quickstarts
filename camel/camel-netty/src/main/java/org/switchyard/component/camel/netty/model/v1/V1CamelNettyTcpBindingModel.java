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
package org.switchyard.component.camel.netty.model.v1;

import org.switchyard.component.camel.netty.model.CamelNettyTcpBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

import static org.switchyard.component.camel.netty.model.Constants.NETTY_NAMESPACE_V1;

/**
 * Implementation of netty binding.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelNettyTcpBindingModel extends V1CamelNettyBindingModel
    implements CamelNettyTcpBindingModel {

    /**
     * Protocol scheme.
     */
    public static final String TCP = "tcp";

    private static final String TEXTLINE = "textline";
    private static final String TCP_NO_DELAY = "tcpNoDelay";
    private static final String KEEP_ALIVE = "keepAlive";


    /**
     * Create a new CamelDirectBindingModel.
     */
    public V1CamelNettyTcpBindingModel() {
        super(TCP, NETTY_NAMESPACE_V1);
        setModelChildrenOrder(TEXTLINE, TCP_NO_DELAY, KEEP_ALIVE);
    }

    /**
     * Create a V1CamelNettyBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelNettyTcpBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public Boolean isTextline() {
        return getBooleanConfig(TEXTLINE);
    }

    @Override
    public V1CamelNettyTcpBindingModel setTextline(Boolean textline) {
        return setConfig(TEXTLINE, textline);
    }

    @Override
    public Boolean isTcpNoDelay() {
        return getBooleanConfig(TCP_NO_DELAY);
    }

    @Override
    public V1CamelNettyTcpBindingModel setTcpNoDelay(Boolean tcpNoDelay) {
        return setConfig(TCP_NO_DELAY, tcpNoDelay);
    }

    @Override
    public Boolean isKeepAlive() {
        return getBooleanConfig(KEEP_ALIVE);
    }

    @Override
    public V1CamelNettyTcpBindingModel setKeepAlive(Boolean keepAlive) {
        return setConfig(KEEP_ALIVE, keepAlive);
    }

    @Override
    protected String getProtocol() {
        return TCP;
    }

}
