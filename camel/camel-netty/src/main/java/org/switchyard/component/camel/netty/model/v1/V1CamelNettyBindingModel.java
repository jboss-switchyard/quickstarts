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

import java.net.URI;
import java.util.List;

import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.netty.model.CamelNettyBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of netty binding.
 * 
 * @author Lukasz Dywicki
 */
public abstract class V1CamelNettyBindingModel extends V1BaseCamelBindingModel
    implements CamelNettyBindingModel {

    /**
     * Camel component prefix.
     */
    public static final String NETTY = "netty";

    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String RECEIVE_BUFFER_SIZE = "receiveBufferSize";
    private static final String SEND_BUFFER_SIZE = "sendBufferSize";
    private static final String REUSE_ADDRESS = "reuseAddress";
    private static final String ENCODERS = "encoders";
    private static final String DECODERS = "decoders";
    private static final String ALLOW_DEFAULT_CODEC = "allowDefaultCodec";
    private static final String WORKER_COUNT = "workerCount";
    private static final String SYNC = "sync";
    private static final String DISCONNECT = "disconnect";

    /**
     * Create a V1CamelNettyBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    protected V1CamelNettyBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    protected V1CamelNettyBindingModel(String name, String namespace) {
        super(name, namespace);

        setModelChildrenOrder(HOST, PORT, RECEIVE_BUFFER_SIZE, SEND_BUFFER_SIZE,
                REUSE_ADDRESS, ENCODERS, DECODERS, ALLOW_DEFAULT_CODEC, WORKER_COUNT,
                SYNC, DISCONNECT);
    }

    @Override
    public String getHost() {
        return getConfig(HOST);
    }

    @Override
    public V1CamelNettyBindingModel setHost(String host) {
        return setConfig(HOST, host);
    }

    @Override
    public Integer getPort() {
        return getIntegerConfig(PORT);
    }

    @Override
    public V1CamelNettyBindingModel setPort(int port) {
        return setConfig(PORT, port);
    }

    @Override
    public Long getReceiveBufferSize() {
        return getLongConfig(RECEIVE_BUFFER_SIZE);
    }

    @Override
    public V1CamelNettyBindingModel setReceiveBufferSize(Long receiveBufferSize) {
        return setConfig(RECEIVE_BUFFER_SIZE, receiveBufferSize);
    }

    @Override
    public Long getSendBufferSize() {
        return getLongConfig(SEND_BUFFER_SIZE);
    }

    @Override
    public V1CamelNettyBindingModel setSendBufferSize(Long sendBufferSize) {
        return setConfig(SEND_BUFFER_SIZE, sendBufferSize);
    }

    @Override
    public Boolean isReuseAddress() {
        return getBooleanConfig(REUSE_ADDRESS);
    }

    @Override
    public V1CamelNettyBindingModel setReuseAddress(Boolean reuseAddress) {
        return setConfig(REUSE_ADDRESS, reuseAddress);
    }

    @Override
    public String getEncoders() {
        return getConfig(ENCODERS);
    }

    @Override
    public V1CamelNettyBindingModel setEncoders(String encoders) {
        return setConfig(ENCODERS, encoders);
    }

    @Override
    public String getDecoders() {
        return getConfig(DECODERS);
    }

    @Override
    public V1CamelNettyBindingModel setDecoders(String decoders) {
        return setConfig(DECODERS, decoders);
    }

    @Override
    public Boolean isAllowDefaultCodec() {
        return getBooleanConfig(ALLOW_DEFAULT_CODEC);
    }

    @Override
    public V1CamelNettyBindingModel setAllowDefaultCodec(Boolean allowDefaultCodec) {
        return setConfig(ALLOW_DEFAULT_CODEC, allowDefaultCodec);
    }

    @Override
    public Integer getWorkerCount() {
        return getIntegerConfig(WORKER_COUNT);
    }

    @Override
    public V1CamelNettyBindingModel setWorkerCount(Integer workerCount) {
        return setConfig(WORKER_COUNT, workerCount);
    }

    @Override
    public Boolean isSync() {
        return getBooleanConfig(SYNC);
    }

    @Override
    public V1CamelNettyBindingModel setSync(Boolean sync) {
        return setConfig(SYNC, sync);
    }

    @Override
    public Boolean isDisconnect() {
        return getBooleanConfig(DISCONNECT);
    }

    @Override
    public V1CamelNettyBindingModel setDisconnect(Boolean disconnect) {
        return setConfig(DISCONNECT, disconnect);
    }

    protected abstract String getProtocol();

    @Override
    public URI getComponentURI() {
        Configuration modelConfiguration = getModelConfiguration();
        List<Configuration> children = modelConfiguration.getChildren();

        String baseUri = NETTY + ":" + getProtocol() + "://" + getHost() + ":" + getPort();

        QueryString queryStr = new QueryString();
        traverseConfiguration(children, queryStr, HOST, PORT);

        return URI.create(baseUri + queryStr.toString());
    }

}
