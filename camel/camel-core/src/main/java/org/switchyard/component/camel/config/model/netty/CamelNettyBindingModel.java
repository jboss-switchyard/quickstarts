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
package org.switchyard.component.camel.config.model.netty;

import org.switchyard.component.camel.config.model.CamelBindingModel;

/**
 * Configuration binding for netty.
 * 
 * @author Lukasz Dywicki
 */
public interface CamelNettyBindingModel extends CamelBindingModel {

    /**
     * Gets host name / ip.
     * 
     * @return Host name.
     */
    String getHost();

    /**
     * Sets host name.
     * 
     * @param host Host name.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setHost(String host);

    /**
     * Gets connection port.
     * 
     * @return Port number used to connect remote server.
     */
    Integer getPort();

    /**
     * Sets port to use during connection.
     * 
     * @param port Port number.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setPort(int port);

    /**
     * The TCP/UDP buffer sizes to be used during inbound communication. Size is bytes.
     * 
     * @return Inbound buffer size.
     */
    Long getReceiveBufferSize();

    /**
     * Specify inbound buffer size.
     * 
     * @param receiveBufferSize Buffer size.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setReceiveBufferSize(Long receiveBufferSize);

    /**
     * The TCP/UDP buffer sizes to be used during outbound communication. Size is bytes.
     * 
     * @return Outbound buffer size.
     */
    Long getSendBufferSize();

    /**
     * Specify outbound buffer size.
     * 
     * @param sendBufferSize Buffer size.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setSendBufferSize(Long sendBufferSize);

    /**
     * Socket multiplexing.
     * 
     * @return True if multiplexing is turned on.
     */
    Boolean isReuseAddress();

    /**
     * Setting to facilitate socket multiplexing.
     * 
     * @param reuseAddress Reuse address.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setReuseAddress(Boolean reuseAddress);

    /**
     * Encoders list containing ChannelDownStreamHandler implementations.
     * 
     * @return Bean name in registry
     */
    String getEncoders();

    /**
     * A list of encoder to be used. You can use a String which have values separated by comma,
     * and have the values be looked up in the Registry. Just remember to prefix the value with
     * # so Camel knows it should look.
     * 
     * @param encoders Name of list in registry.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setEncoders(String encoders);

    /**
     * Encoders list containing ChannelUpStreamHandler implementations.
     * 
     * @return Bean name in registry.
     */
    String getDecoders();

    /**
     * A list of decorder to be used. You can use a String which have values separated by comma,
     * and have the values be looked up in the Registry. Just remember to prefix the value with
     * # so Camel knows it should lookup.
     * 
     * @param decoders Name of list in registry.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setDecoders(String decoders);

    /**
     * Should default codec chanin be used?
     * 
     * @return True to let netty rely on defaults.
     */
    Boolean isAllowDefaultCodec();

    /**
     * The netty component installs a default codec if both, encoder/deocder is null and textline is false.
     * Setting allowDefaultCodec to false prevents the netty component from installing a default codec
     * as the first element in the filter chain.
     * 
     * @param allowDefaultCodec Turn on or off default encoder/decoder chain.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setAllowDefaultCodec(Boolean allowDefaultCodec);

    /**
     * Number of workers to run.
     * 
     * @return Number of workers.
     */
    Integer getWorkerCount();

    /**
     * When netty works on nio mode, it uses default workerCount parameter from Netty, which is cpu_core_threads*2.
     * User can use this operation to override the default workerCount from Netty.
     * 
     * @param workerCount Number of workers.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setWorkerCount(Integer workerCount);

    /**
     * Flag to identify in-out or in only endpoint.
     * 
     * @return True if endpoint is in-out.
     */
    Boolean isSync();

    /**
     * Setting to set endpoint as one-way or request-response.
     * 
     * @param sync Should communication be bidirectional or not?
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setSync(Boolean sync);

    /**
     * Disconnect after operation.
     * 
     * @return True if connection should be closed after use.
     */
    Boolean isDisconnect();

    /**
     * Whether or not to disconnect(close) from Netty Channel right after use.
     * 
     * @param disconnect Close connection after operation.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setDisconnect(Boolean disconnect);

}
