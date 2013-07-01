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

import static junit.framework.Assert.assertEquals;

import org.apache.camel.component.netty.NettyEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;

/**
 * Test for {@link V1CamelNettyBindingModel}.
 *
 * @author Lukasz Dywicki
 */
public class V1CamelNettyTcpBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelNettyTcpBindingModel, NettyEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-netty-tcp-binding-beans.xml";

    private static final String HOST = "google.com";
    private static final Integer PORT = 10230;
    private static final Long RECEIVE_BUFFER_SIZE = 1024l;
    private static final Long SEND_BUFFER_SIZE = 128l;
    private static final Boolean SSL = true;
    private static final String SSL_HANDLER = "#myCustomHandler";
    private static final String PASSPHRASE = "camelRider";
    private static final String SECURITY_PROVIDER = "BC";
    private static final String KEY_STORE_FORMAT = "PCKS12";
    private static final String KEY_STORE_FILE = "#ks";
    private static final String TRUST_STORE_FILE = "#ts";
    private static final Boolean REUSE_ADDRESS = true;
    private static final Boolean ALLOW_DEFAULT_CODEC = false;
    private static final Integer WORKER_COUNT = 10;
    private static final Boolean SYNC = false;
    private static final Boolean DISCONNECT = true;
    private static final Boolean TEXTLINE = false;
    private static final Boolean TCP_NO_DELAY = true;
    private static final Boolean KEEP_ALIVE = false;

    private static final String CAMEL_URI = "netty:tcp://google.com:10230?" +
        "receiveBufferSize=1024&sendBufferSize=128&reuseAddress=true&allowDefaultCodec=false&" +
        "workerCount=10&sync=false&disconnect=true&textline=false&tcpNoDelay=true&" +
        "keepAlive=false&keyStoreFormat=PCKS12&passphrase=camelRider&keyStoreFile=#ks&trustStoreFile=#ts&" +
        "ssl=true&sslHandler=#myCustomHandler&securityProvider=BC";

    public V1CamelNettyTcpBindingModelTest() {
        super(NettyEndpoint.class, CAMEL_XML);

        setSkipCamelEndpointTesting(true);
    }

    @Override
    protected V1CamelNettyTcpBindingModel createTestModel() {
        return ((V1CamelNettyTcpBindingModel) new V1CamelNettyTcpBindingModel()
            .setHost(HOST)
            .setPort(PORT)
            .setReceiveBufferSize(RECEIVE_BUFFER_SIZE)
            .setSendBufferSize(SEND_BUFFER_SIZE)
            .setSsl(SSL)
            .setSslHandler(SSL_HANDLER)
            .setPassphrase(PASSPHRASE)
            .setSecurityProvider(SECURITY_PROVIDER)
            .setKeyStoreFormat(KEY_STORE_FORMAT)
            .setKeyStoreFile(KEY_STORE_FILE)
            .setTrustStoreFile(TRUST_STORE_FILE)
            .setReuseAddress(REUSE_ADDRESS)
            .setAllowDefaultCodec(ALLOW_DEFAULT_CODEC)
            .setWorkerCount(WORKER_COUNT)
            .setSync(SYNC)
            .setDisconnect(DISCONNECT))
            .setTextline(TEXTLINE)
            .setTcpNoDelay(TCP_NO_DELAY)
            .setKeepAlive(KEEP_ALIVE)
            ;
    }

    @Override
    protected void createModelAssertions(V1CamelNettyTcpBindingModel model) {
        assertEquals(HOST, model.getHost());
        assertEquals(PORT, model.getPort());
        assertEquals(RECEIVE_BUFFER_SIZE, model.getReceiveBufferSize());
        assertEquals(SEND_BUFFER_SIZE, model.getSendBufferSize());
        assertEquals(SSL, model.isSsl());
        assertEquals(SSL_HANDLER, model.getSslHandler());
        assertEquals(SECURITY_PROVIDER, model.getSecurityProvider());
        assertEquals(PASSPHRASE, model.getPassphrase());
        assertEquals(KEY_STORE_FORMAT, model.getKeyStoreFormat());
        assertEquals(KEY_STORE_FILE, model.getKeyStoreFile());
        assertEquals(TRUST_STORE_FILE, model.getTrustStoreFile());
        assertEquals(REUSE_ADDRESS, model.isReuseAddress());
        assertEquals(ALLOW_DEFAULT_CODEC, model.isAllowDefaultCodec());
        assertEquals(WORKER_COUNT, model.getWorkerCount());
        assertEquals(SYNC, model.isSync());
        assertEquals(DISCONNECT, model.isDisconnect());
        assertEquals(TEXTLINE, model.isTextline());
        assertEquals(TCP_NO_DELAY, model.isTcpNoDelay());
        assertEquals(KEEP_ALIVE, model.isKeepAlive());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }
}