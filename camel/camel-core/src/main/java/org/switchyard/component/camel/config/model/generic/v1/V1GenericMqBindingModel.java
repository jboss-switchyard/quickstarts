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
package org.switchyard.component.camel.config.model.generic.v1;

import java.net.URI;
import java.util.List;

import org.switchyard.component.camel.config.model.QueryString;
import org.switchyard.component.camel.config.model.generic.GenericMqBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * First implementation of MQ binding.
 * 
 * @author: <a href="mailto:eduardo.devera@gmail.com">Eduardo de Vera</a>
 */
public abstract class V1GenericMqBindingModel extends V1BaseCamelBindingModel implements GenericMqBindingModel {

    protected static final String QUEUE = "queue";
    protected static final String TOPIC = "topic";
    protected static final String CONNECTION_FACTORY = "connectionFactory";
    protected static final String USERNAME = "username";
    protected static final String PASSWORD = "password";
    protected static final String CLIENT_ID = "clientId";
    protected static final String DURABLE_SUBSCRIPTION_NAME = "durableSubscriptionName";
    protected static final String CONCURRENT_CONSUMERS = "concurrentConsumers";
    protected static final String MAX_CONCURRENT_CONSUMERS = "maxConcurrentConsumers";
    protected static final String DISABLE_REPLY_TO = "disableReplyTo";
    protected static final String PRESERVE_MESSAGE_QOS = "preserveMessageQos";
    protected static final String DELIVERY_PERSISTENT = "deliveryPersistent";
    protected static final String PRIORITY = "priority";
    protected static final String EXPLICIT_QOS_ENABLED = "explicitQosEnabled";
    protected static final String REPLY_TO = "replyTo";
    protected static final String REPLY_TO_TYPE = "replyToType";
    protected static final String REQUEST_TIMEOUT = "requestTimeout";
    protected static final String SELECTOR = "selector";
    protected static final String TIME_TO_LIVE = "timeToLive";
    protected static final String TRANSACTED = "transacted";
    protected static final String TRANSACTION_MANAGER = "transactionManager";

    /**
     * Create new binding model using given prefix.
     * 
     * @param type Camel component prefix/binding prefix.
     */
    protected V1GenericMqBindingModel(String type) {
        super(type);
        setModelChildrenOrder(QUEUE, TOPIC, CONNECTION_FACTORY, USERNAME, PASSWORD,
                CLIENT_ID, DURABLE_SUBSCRIPTION_NAME, CONCURRENT_CONSUMERS, MAX_CONCURRENT_CONSUMERS,
                DISABLE_REPLY_TO, PRESERVE_MESSAGE_QOS, DELIVERY_PERSISTENT, REPLY_TO, REPLY_TO_TYPE,
                REQUEST_TIMEOUT, SELECTOR, TIME_TO_LIVE, TRANSACTED, TRANSACTION_MANAGER);
    }

    /**
     * Create a binding model from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param descriptor The switchyard descriptor instance.
     */
    public V1GenericMqBindingModel(Configuration config, Descriptor descriptor) {
        super(config, descriptor);
    }

    @Override
    public String getQueue() {
        return getConfig(QUEUE);
    }

    @Override
    public V1GenericMqBindingModel setQueue(String queue) {
        return setConfig(QUEUE, queue);
    }

    @Override
    public String getTopic() {
        return getConfig(TOPIC);
    }

    @Override
    public V1GenericMqBindingModel setTopic(String topic) {
        return setConfig(TOPIC, topic);
    }

    @Override
    public String getConnectionFactory() {
        return getConfig(CONNECTION_FACTORY);
    }

    @Override
    public V1GenericMqBindingModel setConnectionFactory(String connectionFactory) {
        return setConfig(CONNECTION_FACTORY, connectionFactory);
    }

    @Override
    public String getUsername() {
        return getConfig(USERNAME);
    }

    @Override
    public V1GenericMqBindingModel setUsername(String username) {
        return setConfig(USERNAME, username);
    }

    @Override
    public String getPassword() {
        return getConfig(PASSWORD);
    }

    @Override
    public V1GenericMqBindingModel setPassword(String password) {
        return setConfig(PASSWORD, password);
    }

    @Override
    public String getClientId() {
        return getConfig(CLIENT_ID);
    }

    @Override
    public V1GenericMqBindingModel setClientId(String clientId) {
        return setConfig(CLIENT_ID, clientId);
    }

    @Override
    public String getDurableSubscriptionName() {
        return getConfig(DURABLE_SUBSCRIPTION_NAME);
    }

    @Override
    public V1GenericMqBindingModel setDurableSubscriptionName(String durableSubscriptionName) {
        return setConfig(DURABLE_SUBSCRIPTION_NAME, durableSubscriptionName);
    }

    @Override
    public Integer getConcurrentConsumers() {
        return getIntegerConfig(CONCURRENT_CONSUMERS);
    }

    @Override
    public V1GenericMqBindingModel setConcurrentConsumers(Integer concurrentConsumers) {
        return setConfig(CONCURRENT_CONSUMERS, concurrentConsumers);
    }

    @Override
    public Integer getMaxConcurrentConsumers() {
        return getIntegerConfig(MAX_CONCURRENT_CONSUMERS);
    }

    @Override
    public V1GenericMqBindingModel setMaxConcurrentConsumers(Integer maxConcurrentConsumers) {
        return setConfig(MAX_CONCURRENT_CONSUMERS, maxConcurrentConsumers);
    }

    @Override
    public Boolean isDisableReplyTo() {
        return getBooleanConfig(DISABLE_REPLY_TO);
    }

    @Override
    public V1GenericMqBindingModel setDisableReplyTo(Boolean disableReplyTo) {
        return setConfig(DISABLE_REPLY_TO, disableReplyTo);
    }

    @Override
    public Boolean isPreserveMessageQos() {
        return getBooleanConfig(PRESERVE_MESSAGE_QOS);
    }

    @Override
    public V1GenericMqBindingModel setPreserveMessageQos(Boolean preserveMessageQos) {
        return setConfig(PRESERVE_MESSAGE_QOS, preserveMessageQos);
    }

    @Override
    public Boolean isDeliveryPersistent() {
        return getBooleanConfig(DELIVERY_PERSISTENT);
    }

    @Override
    public V1GenericMqBindingModel setDeliveryPersistent(Boolean deliveryPersistent) {
        return setConfig(DELIVERY_PERSISTENT, deliveryPersistent);
    }

    @Override
    public Integer getPriority() {
        return getIntegerConfig(PRIORITY);
    }

    @Override
    public V1GenericMqBindingModel setPriority(Integer priority) {
        return setConfig(PRIORITY, priority);
    }

    @Override
    public Boolean isExplicitQosEnabled() {
        return getBooleanConfig(EXPLICIT_QOS_ENABLED);
    }

    @Override
    public V1GenericMqBindingModel setExplicitQosEnabled(Boolean explicitQosEnabled) {
        return setConfig(EXPLICIT_QOS_ENABLED, explicitQosEnabled);
    }

    @Override
    public String getReplyTo() {
        return getConfig(REPLY_TO);
    }

    @Override
    public V1GenericMqBindingModel setReplyTo(String replyTo) {
        return setConfig(REPLY_TO, replyTo);
    }

    @Override
    public String getReplyToType() {
        return getConfig(REPLY_TO_TYPE);
    }

    @Override
    public V1GenericMqBindingModel setReplyToType(String replyToType) {
        return setConfig(REPLY_TO_TYPE, replyToType);
    }

    @Override
    public Integer getRequestTimeout() {
        return getIntegerConfig(REQUEST_TIMEOUT);
    }

    @Override
    public V1GenericMqBindingModel setRequestTimeout(Integer requestTimeout) {
        return setConfig(REQUEST_TIMEOUT, requestTimeout);
    }

    @Override
    public String getSelector() {
        return getConfig(SELECTOR);
    }

    @Override
    public V1GenericMqBindingModel setSelector(String selector) {
        return setConfig(SELECTOR, selector);
    }

    @Override
    public Integer getTimeToLive() {
        return getIntegerConfig(TIME_TO_LIVE);
    }

    @Override
    public V1GenericMqBindingModel setTimeToLive(Integer timeToLive) {
        return setConfig(TIME_TO_LIVE, timeToLive);
    }

    @Override
    public Boolean isTransacted() {
        return getBooleanConfig(TRANSACTED);
    }

    @Override
    public V1GenericMqBindingModel setTransacted(Boolean transacted) {
        return setConfig(TRANSACTED, transacted);
    }

    @Override
    public String getTransactionManager() {
        return getConfig(TRANSACTION_MANAGER);
    }

    @Override
    public V1GenericMqBindingModel setTransactionManager(String transactionManager) {
        return setConfig(TRANSACTION_MANAGER, transactionManager);
    }

    /**
     * Gets configuration uri for this binding.
     * 
     * @param type Prefix of camel component to use.
     * @return Complete uri to use for creation of Camel endpoint.
     */
    public final URI getComponentURI(String type) {
        Configuration modelConfiguration = getModelConfiguration();
        List<Configuration> children = modelConfiguration.getChildren();

        String baseUri = type + ":";
        if (getQueue() != null) {
            baseUri += "queue:" + getQueue();
        } else if (getTopic() != null) {
            baseUri += "topic:" + getTopic();
        }

        QueryString queryStr = new QueryString();
        traverseConfiguration(children, queryStr, QUEUE, TOPIC);

        return URI.create(baseUri + queryStr.toString());
    }
}
