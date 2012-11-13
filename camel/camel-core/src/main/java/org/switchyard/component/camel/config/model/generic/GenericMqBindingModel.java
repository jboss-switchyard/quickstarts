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
package org.switchyard.component.camel.config.model.generic;

import org.switchyard.component.camel.config.model.CamelBindingModel;

/**
 * Generic configuration of MQ component.
 * 
 * @author: <a href="mailto:eduardo.devera@gmail.com">Eduardo de Vera</a>
 */
public interface GenericMqBindingModel extends CamelBindingModel {

    /**
     * Get JMS queue name to consume from/produce to.
     *
     * @return destination name.
     */
    String getQueue();

    /**
     * Specify the queue name to consume from.
     *
     * @param queue Queue name.
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setQueue(String queue);

    /**
     * Get JMS queue name to consume from/produce to.
     *
     * @return destination name.
     */
    String getTopic();

    /**
     * Specify the topic name to consume from.
     *
     * @param topic Topic name.
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setTopic(String topic);

    /**
     * Connection factory used to consume/produce messages.
     *
     * @return Connection factory.
     */
    String getConnectionFactory();

    /**
     * Specify connection factory instance name to use.
     *
     * @param connectionFactory Name of bean from camel registry.
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setConnectionFactory(String connectionFactory);

    /**
     * The username for the connection factory.
     *
     * @return Username for connection factory.
     */
    String getUsername();

    /**
     * Specify username for connection factory.
     *
     * @param username Username.
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setUsername(String username);

    /**
     * The password for the connector factory.
     *
     * @return Password used to authorize user.
     */
    String getPassword();

    /**
     * Specify password to use.
     *
     * @param password Password.
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setPassword(String password);

    /**
     * Obtain client id.
     *
     * @return Client id set for connection.
     */
    String getClientId();

    /**
     * Sets the JMS client ID to use. Note that this value, if specified, must be unique and
     * can only be used by a single JMS connection instance. It is typically only required
     * for durable topic subscriptions.
     *
     * @param clientId Client id.
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setClientId(String clientId);

    /**
     * The durable subscriber name for specifying durable topic subscriptions.
     * The clientId option must be configured as well.
     *
     * @return Subscription name.
     */
    String getDurableSubscriptionName();

    /**
     * Specify name of subscription created by camel endpoint.
     *
     * @param durableSubscriptionName Subscription name.
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setDurableSubscriptionName(String durableSubscriptionName);

    /**
     * Get number of concurrent consumers.
     *
     * @return Concurrent consumers count.
     */
    Integer getConcurrentConsumers();

    /**
     * Specifies the default number of concurrent consumers.
     *
     * @param concurrentConsumers Number of concurrent consumers
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setConcurrentConsumers(Integer concurrentConsumers);

    /**
     * Number of maximum consumers.
     *
     * @return Maximum number of concurrent consumers.
     */
    Integer getMaxConcurrentConsumers();

    /**
     * Specifies the maximum number of concurrent consumers.
     *
     * @param maxConcurrentConsumers Maximum number of consumers to use.
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setMaxConcurrentConsumers(Integer maxConcurrentConsumers);

    /**
     * If true, a producer will behave like a InOnly exchange with the exception that
     * JMSReplyTo header is sent out and not be suppressed like in the case of InOnly.
     * Like InOnly the producer will not wait for a reply. A consumer with this flag
     * will behave like InOnly. This feature can be used to bridge InOut requests to
     * another queue so that a route on the other queue will send it´s response directly
     * back to the original JMSReplyTo.
     *
     * @return Should reply to be disabled and JMSReplyTo header be ignored.
     */
    Boolean isDisableReplyTo();

    /**
     * Specify behavior for JMSReplyTo header.
     *
     * @param disableReplyTo True if reply to should be disabled.
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setDisableReplyTo(Boolean disableReplyTo);

    /**
     * Get the way of handling for quality of service related headers.
     *
     * @return True if JMSPriority and JMSDeliveryMode be preserved.
     */
    Boolean isPreserveMessageQos();

    /**
     * Set to true, if you want to send message using the QoS settings specified on the message,
     * instead of the QoS settings on the JMS endpoint. The following three headers are considered
     * JMSPriority, JMSDeliveryMode, and JMSExpiration. You can provide all or only some of them.
     * If not provided, Camel will fall back to use the values from the endpoint instead.
     * So, when using this option, the headers override the values from the endpoint.
     * The explicitQosEnabled option, by contrast, will only use options set on the endpoint,
     * and not values from the message header.
     *
     * @param preserveMessageQos Should JMSPriority and JMSDeliveryMode be preserved.
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setPreserveMessageQos(Boolean preserveMessageQos);

    /**
     * Should message be sent in persistent mode?
     *
     * @return True if delivery should be persistent, false otherwise.
     */
    Boolean isDeliveryPersistent();

    /**
     * Specifies whether persistent delivery is used by default.
     *
     * @param deliveryPersistent Should delivery be persistent.
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setDeliveryPersistent(Boolean deliveryPersistent);

    /**
     * Values greater than 1 specify the message priority when sending
     * (where 0 is the lowest priority and 9 is the highest).
     *
     * @return Priority of message to send.
     */
    Integer getPriority();

    /**
     * Specifies message priority.
     *
     * @param priority Message priority.
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setPriority(Integer priority);

    /**
     * Set if the deliveryMode, priority or timeToLive qualities of service should be used
     * when sending messages. This option is based on Spring's JmsTemplate.
     * The deliveryMode, priority and timeToLive options are applied to the current endpoint.
     * This contrasts with the preserveMessageQos option, which operates at message granularity,
     * reading QoS properties exclusively from the Camel In message headers.
     *
     * @param explicitQosEnabled Should QOS be explicitly set?
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setExplicitQosEnabled(Boolean explicitQosEnabled);

    /**
     * Get the status of QOS for endpoint.
     *
     * @return True if delivery should be persistent, false otherwise.
     */
    Boolean isExplicitQosEnabled();

    /**
     * Get name of reply destination.
     *
     * @return Reply destination name.
     */
    String getReplyTo();

    /**
     * Provides an explicit ReplyTo destination, which overrides any incoming value
     * of Message.getJMSReplyTo().
     *
     * @param replyTo Name of destination used to send reply.
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setReplyTo(String replyTo);

    /**
     * Get reply to type.
     *
     * @return Type of reply destination.
     */
    String getReplyToType();

    /**
     * Allows for explicitly specifying which kind of strategy to use for replyTo queues
     * when doing request/reply over JMS. Possible values are: Temporary, Shared, or Exclusive.
     * By default Camel will use temporary queues. However if replyTo has been configured,
     * then Shared is used by default. This option allows you to use exclusive queues
     * instead of shared ones.
     *
     * @param replyToType Type of reply to destination (Temporary, Shared or Exclusive).
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setReplyToType(String replyToType);

    /**
     * Producer only: The timeout for waiting for a reply when using the InOut
     * Exchange Pattern (in milliseconds). The default is 20 seconds.
     *
     * @return Request timeout for in out communication.
     */
    Integer getRequestTimeout();

    /**
     * Specify request timeout.
     *
     * @param requestTimeout Request timeout.
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setRequestTimeout(Integer requestTimeout);

    /**
     * JMS Selector to use.
     *
     * @return Selector for message consumption.
     */
    String getSelector();

    /**
     * Sets the JMS Selector, which is an SQL 92 predicate that is used to filter
     * messages within the broker. You may have to encode special characters such as = as %3D
     *
     * @param selector Message selector.
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setSelector(String selector);

    /**
     * Message time to live.
     *
     * @return Time to live.
     */
    Integer getTimeToLive();

    /**
     * Specifies the time-to-live of the message (in milliseconds).
     *
     * @param timeToLive Time to live for message.
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setTimeToLive(Integer timeToLive);

    /**
     * Checks if transaction should be used to consume/receive messages.
     *
     * @return If JMS session is transacted.
     */
    Boolean isTransacted();

    /**
     * Specifies whether to use transacted mode for sending/receiving messages using the InOnly.
     *
     * @param transacted Should session be transacted.
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setTransacted(Boolean transacted);

    /**
     * The Spring transaction manager to use.
     *
     * @return Transaction manager.
     */
    String getTransactionManager();

    /**
     * Specifies transaction manager to use with endpoint.
     *
     * @param transactionManager Spring transaction manager bean.
     * @return a reference to this Camel binding model
     */
    GenericMqBindingModel setTransactionManager(String transactionManager);
}
