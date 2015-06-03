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
package org.switchyard.component.camel.mqtt.model;

import org.switchyard.component.camel.common.model.CamelBindingModel;

/**
 * Configuration binding for mqtt.
 * 
 * @author Douglas Palmer
 */
public interface CamelMqttBindingModel extends CamelBindingModel {

    /** Camel endpoint type. */
    String MQTT = "mqtt";

    /**
     * Gets name.
     * 
     * @return name
     */
    String getName();

    /**
     * Sets name.
     * 
     * @param name name
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setName(String name);

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
    CamelMqttBindingModel setHost(String host);

    /**
     * Gets local address.
     * 
     * @return local address
     */
    String getLocalAddress();

    /**
     * Sets local address.
     * @param localAddress local address
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setLocalAddress(String localAddress);

    /**
     * Gets connectAttemptsMax.
     * 
     * @return connectAttemptsMax
     */
    Integer getConnectAttemptsMax();

    /**
     * Sets the maximum number of connect attempts.
     * 
     * @param connectAttemptsMax Maximum number of connect attempts.
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setConnectAttemptsMax(int connectAttemptsMax);

    /**
     * Gets reconnectAttemptsMax.
     * 
     * @return reconnectAttemptsMax
     */
    Integer getReconnectAttemptsMax();

    /**
     * Sets the maximum number of reconnect attempts.
     * 
     * @param reconnectAttemptsMax Maximum number of reconnect attempts.
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setReconnectAttemptsMax(int reconnectAttemptsMax);

    /**
     * Gets reconnect delay.
     * 
     * @return reconnectDelay
     */
    Integer getReconnectDelay();

    /**
     * Sets the delay between reconnects.
     * 
     * @param reconnectDelay the time between reconnect attempts.
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setReconnectDelay(int reconnectDelay);

    /**
     * Gets multiplier for incremental backoff between reconnect attempts.
     * 
     * @return reconnectBackOffMultiplier
     */
    Double getReconnectBackOffMultiplier();

    /**
     * Sets the multiplier for incremental backoff between reconnect attempts.
     * 
     * @param reconnectBackOffMultiplier the multiplier for incremental backoff between reconnect attempts.
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setReconnectBackOffMultiplier(double reconnectBackOffMultiplier);

    /**
     * Gets the maximum reconnect delay.
     * 
     * @return reconnectDelayMax
     */
    Integer getReconnectDelayMax();

    /**
     * Sets the maximum delay between reconnects.
     * 
     * @param reconnectDelayMax the maximum time between reconnect attempts.
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setReconnectDelayMax(int reconnectDelayMax);

    /**
     * Gets user name.
     * @return user name
     */
    String getUserName();

    /**
     * Sets user name.
     * @param username user name
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setUserName(String username);

    /**
     * Gets password.
     * @return password
     */
    String getPassword();

    /**
     * Sets password.
     * @param password password
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setPassword(String password);

    /**
     * Gets quality of service.
     * 
     * @return quality of service
     */
    String getQualityOfService();

    /**
     * Sets quality of service.
     * @param qualityOfService quality of service
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setQualityOfService(String qualityOfService);

    /**
     * Gets subscribe topic name.
     * @return subscribe topic name
     */
    String getSubscribeTopicName();

    /**
     * Sets subscribe topic name.
     * 
     * @param subscribeTopicName subscribe topic name
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setSubscribeTopicName(String subscribeTopicName);

    /**
     * Gets publish topic name.
     * 
     * @return publish topic name
     */
    String getPublishTopicName();

    /**
     * Sets publish topic name.
     * @param publishTopicName publish topic name
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setPublishTopicName(String publishTopicName);

    /**
     * Retain messages by default - on/off.
     * 
     * @return True if messages should be retained by default.
     */
    Boolean isByDefaultRetain();

    /**
     * Setting to specify whether messages should be retained by default.
     * 
     * @param byDefaultRetain Should messages be retained by default.
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setByDefaultRetain(Boolean byDefaultRetain);

    /**
     * Gets mqtt topic property name.
     * 
     * @return topic property name
     */
    String getMqttTopicPropertyName();

    /**
     * Sets mqtt topic property name.
     * 
     * @param mqttTopicPropertyName topic property name
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setMqttTopicPropertyName(String mqttTopicPropertyName);

    /**
     * Gets mqtt retain property name.
     * 
     * @return retain property name
     */
    String getMqttRetainPropertyName();

    /**
     * Sets mqtt retain property name.
     * 
     * @param mqttRetainPropertyName retain property name
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setMqttRetainPropertyName(String mqttRetainPropertyName);

    /**
     * Gets mqtt QoS property name.
     * 
     * @return QoS property name
     */
    String getMqttQosPropertyName();

    /**
     * Sets mqtt QoS property name.
     * 
     * @param mqttQosPropertyName QoS property name
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setMqttQosPropertyName(String mqttQosPropertyName);

    /**
     * Gets connect wait in seconds.
     * 
     * @return connect wait in seconds
     */
    Integer getConnectWaitInSeconds();

    /**
     * Sets connect wait in seconds.
     * 
     * @param connectWaitInSeconds connect wait in seconds
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setConnectWaitInSeconds(int connectWaitInSeconds);

    /**
     * Gets disconnect wait in seconds.
     * 
     * @return disconnect wait in seconds
     */
    Integer getDisconnectWaitInSeconds();

    /**
     * Sets disconnect wait in seconds.
     * 
     * @param disconnectWaitInSeconds disconnect wait in seconds
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setDisonnectWaitInSeconds(int disconnectWaitInSeconds);

    /**
     * Gets send wait in seconds.
     * 
     * @return send wait in seconds
     */
    Integer getSendWaitInSeconds();

    /**
     * Sets send wait in seconds.
     * 
     * @param sendWaitInSeconds send wait in seconds
     * @return a reference to this binding model
     */
    CamelMqttBindingModel setSendWaitInSeconds(int sendWaitInSeconds);

}
