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
package org.switchyard.component.camel.jms.model.v1;

import static org.switchyard.component.camel.jms.model.Constants.JMS_NAMESPACE_V1;

import java.net.URI;
import java.util.List;

import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.jms.model.CamelJmsBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of {@link CamelJmsBindingModel}.
 */
public class V1CamelJmsBindingModel extends V1BaseCamelBindingModel
    implements CamelJmsBindingModel {

    /**
     * Camel endpoint type.
     */
    public static final String JMS = "jms";

    private static final String QUEUE = "queue";
    private static final String TOPIC = "topic";
    private static final String CONNECTION_FACTORY = "connectionFactory";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String CLIENT_ID = "clientId";
    private static final String DURABLE_SUBSCRIPTION_NAME = "durableSubscriptionName";
    private static final String CONCURRENT_CONSUMERS = "concurrentConsumers";
    private static final String MAX_CONCURRENT_CONSUMERS = "maxConcurrentConsumers";
    private static final String DISABLE_REPLY_TO = "disableReplyTo";
    private static final String PRESERVE_MESSAGE_QOS = "preserveMessageQos";
    private static final String DELIVERY_PERSISTENT = "deliveryPersistent";
    private static final String PRIORITY = "priority";
    private static final String EXPLICIT_QOS_ENABLED = "explicitQosEnabled";
    private static final String REPLY_TO = "replyTo";
    private static final String REPLY_TO_TYPE = "replyToType";
    private static final String REQUEST_TIMEOUT = "requestTimeout";
    private static final String SELECTOR = "selector";
    private static final String TIME_TO_LIVE = "timeToLive";
    private static final String TRANSACTED = "transacted";
    private static final String TRANSACTION_MANAGER = "transactionManager";

    /**
     * Create a new CamelJmsBindingModel.
     */
    public V1CamelJmsBindingModel() {
        super(JMS, JMS_NAMESPACE_V1);

        setModelChildrenOrder(QUEUE, TOPIC, CONNECTION_FACTORY, USERNAME, PASSWORD,
            CLIENT_ID, DURABLE_SUBSCRIPTION_NAME, CONCURRENT_CONSUMERS, MAX_CONCURRENT_CONSUMERS,
            DISABLE_REPLY_TO, PRESERVE_MESSAGE_QOS, DELIVERY_PERSISTENT, PRIORITY,
            EXPLICIT_QOS_ENABLED, REPLY_TO, REPLY_TO_TYPE, REQUEST_TIMEOUT, SELECTOR,
            TIME_TO_LIVE, TRANSACTED, TRANSACTION_MANAGER);
    }

    /**
     * Create a binding model from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param descriptor The switchyard descriptor instance.
     */
    public V1CamelJmsBindingModel(Configuration config, Descriptor descriptor) {
        super(config, descriptor);
    }

    /**
     * Extension constructor.
     * 
     * @param type Type of binding.
     * @param namespace Binding namespace.
     */
    protected V1CamelJmsBindingModel(String type, String namespace) {
        super(type, namespace);
    }

    @Override
    public String getQueue() {
        return getConfig(QUEUE);
    }

    @Override
    public V1CamelJmsBindingModel setQueue(String queue) {
        return setConfig(QUEUE, queue);
    }

    @Override
    public String getTopic() {
        return getConfig(TOPIC);
    }

    @Override
    public V1CamelJmsBindingModel setTopic(String topic) {
        return setConfig(TOPIC, topic);
    }

    @Override
    public String getConnectionFactory() {
        return getConfig(CONNECTION_FACTORY);
    }

    @Override
    public V1CamelJmsBindingModel setConnectionFactory(String connectionFactory) {
        return setConfig(CONNECTION_FACTORY, connectionFactory);
    }

    @Override
    public String getUsername() {
        return getConfig(USERNAME);
    }

    @Override
    public V1CamelJmsBindingModel setUsername(String username) {
        return setConfig(USERNAME, username);
    }

    @Override
    public String getPassword() {
        return getConfig(PASSWORD);
    }

    @Override
    public V1CamelJmsBindingModel setPassword(String password) {
        return setConfig(PASSWORD, password);
    }

    @Override
    public String getClientId() {
        return getConfig(CLIENT_ID);
    }

    @Override
    public V1CamelJmsBindingModel setClientId(String clientId) {
        return setConfig(CLIENT_ID, clientId);
    }

    @Override
    public String getDurableSubscriptionName() {
        return getConfig(DURABLE_SUBSCRIPTION_NAME);
    }

    @Override
    public V1CamelJmsBindingModel setDurableSubscriptionName(String durableSubscriptionName) {
        return setConfig(DURABLE_SUBSCRIPTION_NAME, durableSubscriptionName);
    }

    @Override
    public Integer getConcurrentConsumers() {
        return getIntegerConfig(CONCURRENT_CONSUMERS);
    }

    @Override
    public V1CamelJmsBindingModel setConcurrentConsumers(Integer concurrentConsumers) {
        return setConfig(CONCURRENT_CONSUMERS, concurrentConsumers);
    }

    @Override
    public Integer getMaxConcurrentConsumers() {
        return getIntegerConfig(MAX_CONCURRENT_CONSUMERS);
    }

    @Override
    public V1CamelJmsBindingModel setMaxConcurrentConsumers(Integer maxConcurrentConsumers) {
        return setConfig(MAX_CONCURRENT_CONSUMERS, maxConcurrentConsumers);
    }

    @Override
    public Boolean isDisableReplyTo() {
        return getBooleanConfig(DISABLE_REPLY_TO);
    }

    @Override
    public V1CamelJmsBindingModel setDisableReplyTo(Boolean disableReplyTo) {
        return setConfig(DISABLE_REPLY_TO, disableReplyTo);
    }

    @Override
    public Boolean isPreserveMessageQos() {
        return getBooleanConfig(PRESERVE_MESSAGE_QOS);
    }

    @Override
    public V1CamelJmsBindingModel setPreserveMessageQos(Boolean preserveMessageQos) {
        return setConfig(PRESERVE_MESSAGE_QOS, preserveMessageQos);
    }

    @Override
    public Boolean isDeliveryPersistent() {
        return getBooleanConfig(DELIVERY_PERSISTENT);
    }

    @Override
    public V1CamelJmsBindingModel setDeliveryPersistent(Boolean deliveryPersistent) {
        return setConfig(DELIVERY_PERSISTENT, deliveryPersistent);
    }

    @Override
    public Integer getPriority() {
        return getIntegerConfig(PRIORITY);
    }

    @Override
    public V1CamelJmsBindingModel setPriority(Integer priority) {
        return setConfig(PRIORITY, priority);
    }

    @Override
    public Boolean isExplicitQosEnabled() {
        return getBooleanConfig(EXPLICIT_QOS_ENABLED);
    }

    @Override
    public V1CamelJmsBindingModel setExplicitQosEnabled(Boolean explicitQosEnabled) {
        return setConfig(EXPLICIT_QOS_ENABLED, explicitQosEnabled);
    }

    @Override
    public String getReplyTo() {
        return getConfig(REPLY_TO);
    }

    @Override
    public V1CamelJmsBindingModel setReplyTo(String replyTo) {
        return setConfig(REPLY_TO, replyTo);
    }

    @Override
    public String getReplyToType() {
        return getConfig(REPLY_TO_TYPE);
    }

    @Override
    public V1CamelJmsBindingModel setReplyToType(String replyToType) {
        return setConfig(REPLY_TO_TYPE, replyToType);
    }

    @Override
    public Integer getRequestTimeout() {
        return getIntegerConfig(REQUEST_TIMEOUT);
    }

    @Override
    public V1CamelJmsBindingModel setRequestTimeout(Integer requestTimeout) {
        return setConfig(REQUEST_TIMEOUT, requestTimeout);
    }

    @Override
    public String getSelector() {
        return getConfig(SELECTOR);
    }

    @Override
    public V1CamelJmsBindingModel setSelector(String selector) {
        return setConfig(SELECTOR, selector);
    }

    @Override
    public Integer getTimeToLive() {
        return getIntegerConfig(TIME_TO_LIVE);
    }

    @Override
    public V1CamelJmsBindingModel setTimeToLive(Integer timeToLive) {
        return setConfig(TIME_TO_LIVE, timeToLive);
    }

    @Override
    public Boolean isTransacted() {
        return getBooleanConfig(TRANSACTED);
    }

    @Override
    public V1CamelJmsBindingModel setTransacted(Boolean transacted) {
        return setConfig(TRANSACTED, transacted);
    }

    @Override
    public String getTransactionManager() {
        return getConfig(TRANSACTION_MANAGER);
    }

    @Override
    public V1CamelJmsBindingModel setTransactionManager(String transactionManager) {
        return setConfig(TRANSACTION_MANAGER, transactionManager);
    }

    @Override
    public URI getComponentURI() {
        return getComponentURI(JMS);
    }

    protected URI getComponentURI(String prefix) {
        Configuration modelConfiguration = getModelConfiguration();
        List<Configuration> children = modelConfiguration.getChildren();

        String baseUri = prefix + ":";
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
