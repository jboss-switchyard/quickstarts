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
package org.switchyard.component.camel.ftp.model.v1;

import static junit.framework.Assert.assertEquals;

import org.apache.camel.component.file.remote.FtpEndpoint;
import org.switchyard.component.camel.common.model.remote.CamelRemoteFileBindingModel.PathSeparator;
import org.switchyard.component.camel.common.model.remote.v1.V1CamelRemoteFileConsumerBindingModel;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.ftp.model.CamelFtpNamespace;

/**
 * Test for {@link V1CamelFtpBindingModel}.
 * 
 * @author Lukasz Dywicki
 */
@SuppressWarnings("rawtypes")
public class V1CamelFtpBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelFtpBindingModel, FtpEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-ftp-binding-beans.xml";

    private static final String DIRECTORY = "test";
    private static final Boolean AUTO_CREATE = false;
    private static final String HOST = "localhost";
    private static final Integer PORT = 203;
    private static final String USERNAME = "camel";
    private static final String PASSWORD = "secret";
    private static final Boolean BINARY = true;
    private static final Integer CONNECT_TIMEOUT = 10;
    private static final Boolean DISCONNECT = true;
    private static final Integer MAXIMUM_RECONNECT_ATTEMPTS = 10;
    private static final Integer RECONNECT_DELAY = 10;
    private static final PathSeparator SEPARATOR = PathSeparator.UNIX;
    private static final Boolean STEPWISE = true;
    private static final Boolean THROW_EXCEPTION_ON_CONNECT_FAIL = true;
    private static final Integer TIMEOUT = 10;
    private static final Integer SO_TIMEOUT = 10;
    private static final Boolean PASSIVE_MODE = true;
    private static final String SITE_COMMAND = "PWD";
    private static final Integer INITIAL_DELAY = 500;

    private static final String CAMEL_URI = "ftp://camel:secret@localhost:203/test?autoCreate=false&binary=true&" +
        "connectTimeout=10&disconnect=true&maximumReconnectAttempts=10&reconnectDelay=10&" +
        "separator=UNIX&stepwise=true&throwExceptionOnConnectFailed=true&passiveMode=true&" +
        "timeout=10&soTimeout=10&siteCommand=PWD&initialDelay=500";

    public V1CamelFtpBindingModelTest() {
        super(FtpEndpoint.class, CAMEL_XML);
    }

    @Override
    protected V1CamelFtpBindingModel createTestModel() {
        V1CamelFtpBindingModel model = (V1CamelFtpBindingModel) new V1CamelFtpBindingModel(CamelFtpNamespace.V_1_0.uri())
            .setDirectory(DIRECTORY)
            .setAutoCreate(AUTO_CREATE);
        model.setHost(HOST)
            .setPort(PORT)
            .setUsername(USERNAME)
            .setPassword(PASSWORD)
            .setBinary(BINARY)
            .setConnectionTimeout(CONNECT_TIMEOUT)
            .setDisconnect(DISCONNECT)
            .setMaximumReconnectAttempts(MAXIMUM_RECONNECT_ATTEMPTS)
            .setReconnectDelay(RECONNECT_DELAY)
            .setSeparator(SEPARATOR.name())
            .setStepwise(STEPWISE)
            .setThrowExceptionOnConnectFailed(THROW_EXCEPTION_ON_CONNECT_FAIL);
        model.setPassiveMode(PASSIVE_MODE)
            .setTimeout(TIMEOUT)
            .setSoTimeout(SO_TIMEOUT)
            .setSiteCommand(SITE_COMMAND);
        V1CamelRemoteFileConsumerBindingModel consumer = new V1CamelRemoteFileConsumerBindingModel(CamelFtpNamespace.V_1_0.uri(), V1CamelFtpBindingModel.CONSUME);
        consumer.setInitialDelay(INITIAL_DELAY);
        model.setConsumer(consumer);
        return model;
    }

    @Override
    protected void createModelAssertions(V1CamelFtpBindingModel model) {
        assertEquals(PASSIVE_MODE, model.isPassiveMode());
        assertEquals(TIMEOUT, model.getTimeout());
        assertEquals(SO_TIMEOUT, model.getSoTimeout());
        assertEquals(SITE_COMMAND, model.getSiteCommand());
        assertEquals(DIRECTORY, model.getDirectory());
        assertEquals(HOST, model.getHost());
        assertEquals(PORT, model.getPort());
        assertEquals(USERNAME, model.getUsername());
        assertEquals(PASSWORD, model.getPassword());
        assertEquals(BINARY, model.isBinary());
        assertEquals(CONNECT_TIMEOUT, model.getConnectTimeout());
        assertEquals(THROW_EXCEPTION_ON_CONNECT_FAIL, model.isThrowExceptionOnConnectFailed());
        assertEquals(STEPWISE, model.isStepwise());
        assertEquals(SEPARATOR, model.getSeparator());
        assertEquals(RECONNECT_DELAY, model.getReconnectDelay());
        assertEquals(MAXIMUM_RECONNECT_ATTEMPTS, model.getMaximumReconnectAttempts());
        assertEquals(DISCONNECT, model.getDisconnect());
        assertEquals(INITIAL_DELAY, model.getConsumer().getInitialDelay());
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
