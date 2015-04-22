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
package org.switchyard.component.camel.mail.model.v1;

import java.net.URI;
import java.util.List;

import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.mail.model.CamelMailBindingModel;
import org.switchyard.component.camel.mail.model.CamelMailConsumerBindingModel;
import org.switchyard.component.camel.mail.model.CamelMailProducerBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * First implementation of mail binding.
 */
public class V1CamelMailBindingModel extends V1BaseCamelBindingModel
    implements CamelMailBindingModel {

    /**
     * Binding name.
     */
    public static final String MAIL = "mail";

    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String CONNECTION_TIMEOUT = "connectionTimeout";
    private static final String SECURE = "secure";

    /**
     * Mail consumer binding.
     */
    private V1CamelMailConsumerBindingModel _consume;

    /**
     * Mail producer binding.
     */
    private V1CamelMailProducerBindingModel _produce;

    /**
     * Creates new mail binding model.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelMailBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * Creates new mail binding model.
     * @param namespace namespace
     */
    public V1CamelMailBindingModel(String namespace) {
        super(MAIL, namespace);

        setModelChildrenOrder(HOST, PORT, USERNAME, PASSWORD, CONNECTION_TIMEOUT,
            CONSUME, PRODUCE);
    }

    @Override
    public String getHost() {
        return getConfig(HOST);
    }

    @Override
    public V1CamelMailBindingModel setHost(String host) {
        return setConfig(HOST, host);
    }

    @Override
    public Integer getPort() {
        return getIntegerConfig(PORT);
    }

    @Override
    public V1CamelMailBindingModel setPort(Integer port) {
        return setConfig(PORT, port);
    }

    @Override
    public String getUsername() {
        return getConfig(USERNAME);
    }

    @Override
    public V1CamelMailBindingModel setUsername(String username) {
        return setConfig(USERNAME, username);
    }

    @Override
    public String getPassword() {
        return getConfig(PASSWORD);
    }

    @Override
    public V1CamelMailBindingModel setPassword(String password) {
        return setConfig(PASSWORD, password);
    }

    @Override
    public Integer getConnectionTimeout() {
        return getIntegerConfig(CONNECTION_TIMEOUT);
    }

    @Override
    public V1CamelMailBindingModel setConnectionTimeout(Integer connectionTimeout) {
        return setConfig(CONNECTION_TIMEOUT, connectionTimeout);
    }

    @Override
    public Boolean isSecure() {
        return Boolean.valueOf(getModelAttribute(SECURE));
    }

    @Override
    public V1CamelMailBindingModel setSecure(Boolean secure) {
        setModelAttribute(SECURE, Boolean.toString(secure));
        return this;
    }

    @Override
    public V1CamelMailBindingModel setConsumer(CamelMailConsumerBindingModel consumer) {
        _consume = replaceChildren(CONSUME, (V1CamelMailConsumerBindingModel) consumer);
        return this;
    }

    @Override
    public V1CamelMailConsumerBindingModel getConsumer() {
        if (_consume == null) {
            _consume = new V1CamelMailConsumerBindingModel(getFirstChild(CONSUME), getModelDescriptor());
        }
        return _consume;
    }

    @Override
    public V1CamelMailBindingModel setProducer(CamelMailProducerBindingModel producer) {
        _produce = replaceChildren(PRODUCE, (V1CamelMailProducerBindingModel) producer);
        return this;
    }

    @Override
    public V1CamelMailProducerBindingModel getProducer() {
        if (_produce == null) {
            _produce = new V1CamelMailProducerBindingModel(getFirstChild(PRODUCE), getModelDescriptor());
        }
        return _produce;
    }

    @Override
    public URI getComponentURI() {
        Configuration modelConfiguration = getModelConfiguration();
        List<Configuration> children = modelConfiguration.getChildren();

        QueryString queryString = new QueryString();
        traverseConfiguration(children, queryString, HOST, PORT);

        String protocol = isReferenceBinding() ?  getProducer().getProtocol() : getConsumer().getProtocol();
        String port = getPort() != null ? ":" + getPort() : "";

        return URI.create(protocol + (isSecure() ? "s" : "")  + "://" + getHost() + port + queryString);
    }

}
