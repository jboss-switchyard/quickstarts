/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    private static final String SSL = "ssl";
    private static final String SSL_HANDLER = "sslHandler";
    private static final String NEED_CLIENT_AUTH = "needClientAuth";
    private static final String PASSPHRASE = "passphrase";
    private static final String SECURITY_PROVIDER = "securityProvider";
    private static final String KEY_STORE_FORMAT = "keyStoreFormat";
    private static final String KEY_STORE_FILE = "keyStoreFile";
    private static final String TRUST_STORE_FILE = "trustStoreFile";
    private static final String SSL_CONTEXT_PARAMETERS_REF = "sslContextParametersRef";
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

    protected V1CamelNettyBindingModel(String namespace, String name) {
        super(namespace, name);

        setModelChildrenOrder(HOST, PORT, RECEIVE_BUFFER_SIZE, SEND_BUFFER_SIZE,
                SSL, SSL_HANDLER, NEED_CLIENT_AUTH, PASSPHRASE, SECURITY_PROVIDER, KEY_STORE_FORMAT,
                KEY_STORE_FILE, TRUST_STORE_FILE, SSL_CONTEXT_PARAMETERS_REF, REUSE_ADDRESS,
                ENCODERS, DECODERS, ALLOW_DEFAULT_CODEC, WORKER_COUNT, SYNC, DISCONNECT);
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
    public Boolean isSsl() {
        return getBooleanConfig(SSL);
    }

    @Override
    public V1CamelNettyBindingModel setSsl(Boolean ssl) {
        return setConfig(SSL, ssl);
    }

    @Override
    public String getSslHandler() {
        return getConfig(SSL_HANDLER);
    }

    @Override
    public V1CamelNettyBindingModel setSslHandler(String sslHandler) {
        return setConfig(SSL_HANDLER, sslHandler);
    }

    @Override
    public Boolean isNeedClientAuth() {
        return getBooleanConfig(NEED_CLIENT_AUTH);
    }
    
    @Override
    public V1CamelNettyBindingModel setNeedClientAuth(Boolean needClientAuth) {
        return setConfig(NEED_CLIENT_AUTH, needClientAuth);
    }

    @Override
    public String getPassphrase() {
        return getConfig(PASSPHRASE);
    }

    @Override
    public V1CamelNettyBindingModel setPassphrase(String passphrase) {
        return setConfig(PASSPHRASE, passphrase);
    }

    @Override
    public String getSecurityProvider() {
        return getConfig(SECURITY_PROVIDER);
    }

    @Override
    public V1CamelNettyBindingModel setSecurityProvider(String securityProvider) {
        return setConfig(SECURITY_PROVIDER, securityProvider);
    }

    @Override
    public String getKeyStoreFormat() {
        return getConfig(KEY_STORE_FORMAT);
    }

    @Override
    public V1CamelNettyBindingModel setKeyStoreFormat(String keyStoreFormat) {
        return setConfig(KEY_STORE_FORMAT, keyStoreFormat);
    }

    @Override
    public String getKeyStoreFile() {
        return getConfig(KEY_STORE_FILE);
    }

    @Override
    public V1CamelNettyBindingModel setKeyStoreFile(String keyStoreFile) {
        return setConfig(KEY_STORE_FILE, keyStoreFile);
    }

    @Override
    public String getTrustStoreFile() {
        return getConfig(TRUST_STORE_FILE);
    }

    @Override
    public V1CamelNettyBindingModel setTrustStoreFile(String trustStoreFile) {
        return setConfig(TRUST_STORE_FILE, trustStoreFile);
    }

    @Override
    public String getSslContextParametersRef() {
        return getConfig(SSL_CONTEXT_PARAMETERS_REF);
    }

    @Override
    public V1CamelNettyBindingModel setSslContextParametersRef(String sslContextParametersRef) {
        return setConfig(SSL_CONTEXT_PARAMETERS_REF, sslContextParametersRef);
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
