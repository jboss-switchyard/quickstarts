/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.switchyard.component.camel.config.model.mail.v1;

import java.net.URI;
import java.util.List;

import org.switchyard.component.camel.config.model.QueryString;
import org.switchyard.component.camel.config.model.mail.CamelMailBindingModel;
import org.switchyard.component.camel.config.model.mail.CamelMailConsumerBindingModel;
import org.switchyard.component.camel.config.model.mail.CamelMailProducerBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.CompositeReferenceModel;

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
     */
    public V1CamelMailBindingModel() {
        super(MAIL);

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

        boolean producer = getModelParent() instanceof CompositeReferenceModel;

        String protocol = producer ?  getProducer().getProtocol() : getConsumer().getProtocol();
        String port = getPort() != null ? ":" + getPort() : "";

        return URI.create(protocol + (isSecure() ? "s" : "")  + "://" + getHost() + port + queryString);
    }

}
