package org.switchyard.component.camel.mqtt.model.v2;

import java.net.URI;
import java.util.List;

import org.apache.camel.util.UnsafeUriCharactersEncoder;
import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.mqtt.model.CamelMqttBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * V2 CamelMqttBindingModel.
 */
public class V2CamelMqttBindingModel extends V1BaseCamelBindingModel implements CamelMqttBindingModel {

    /** QoS enum. */
    public enum QualityOfService {
        /** at most once. */
        AtMostOnce,
        /** at least once. */
        AtLeastOnce,
        /** exactly once. */
        ExactlyOnce;
    }

    private static final String NAME = "name";
    private static final String HOST = "host";
    private static final String LOCAL_ADDRESS = "localAddress";
    private static final String CONNECT_ATTEMPTS_MAX = "connectAttemptsMax";
    private static final String RECONNECT_ATTEMPTS_MAX = "reconnectAttemptsMax";
    private static final String RECONNECT_DELAY = "reconnectDelay";
    private static final String RECONNECT_BACK_OFF_MULTIPLIER = "reconnectBackOffMultiplier";
    private static final String RECONNECT_DELAY_MAX = "reconnectDelayMax";
    private static final String USER_NAME = "userName";
    private static final String PASSWORD = "password";
    private static final String QUALITY_OF_SERVICE = "qualityOfService";
    private static final String SUBSCRIBE_TOPIC_NAME = "subscribeTopicName";
    private static final String PUBLISH_TOPIC_NAME = "publishTopicName";
    private static final String BY_DEFAULT_RETAIN = "byDefaultRetain";
    private static final String MQTT_TOPIC_PROPERTY_NAME = "mqttTopicPropertyName";
    private static final String MQTT_RETAIN_PROPERTY_NAME = "mqttRetainPropertyName";
    private static final String MQTT_QOS_PROPERTY_NAME = "mqttQosPropertyName";
    private static final String CONNECT_WAIT_IN_SECONDS = "connectWaitInSeconds";
    private static final String DISCONNECT_WAIT_IN_SECONDS = "disconnectWaitInSeconds";
    private static final String SEND_WAIT_IN_SECONDS = "sendWaitInSeconds";

    /**
     * Creates new mqtt binding model.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V2CamelMqttBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * Creates new mqtt binding model.
     * @param namespace namespace
     */
    public V2CamelMqttBindingModel(String namespace) {
        super(MQTT, namespace);

        setModelChildrenOrder(NAME, HOST, LOCAL_ADDRESS, CONNECT_ATTEMPTS_MAX, RECONNECT_ATTEMPTS_MAX, RECONNECT_DELAY, RECONNECT_BACK_OFF_MULTIPLIER, RECONNECT_DELAY_MAX,
                USER_NAME, PASSWORD, QUALITY_OF_SERVICE, SUBSCRIBE_TOPIC_NAME, PUBLISH_TOPIC_NAME, BY_DEFAULT_RETAIN, MQTT_TOPIC_PROPERTY_NAME, MQTT_RETAIN_PROPERTY_NAME,
                MQTT_QOS_PROPERTY_NAME, CONNECT_WAIT_IN_SECONDS, DISCONNECT_WAIT_IN_SECONDS, SEND_WAIT_IN_SECONDS);
    }
    
    @Override
    public String getName() {
        return getModelAttribute(NAME);
    }

    @Override
    public V2CamelMqttBindingModel setName(String name) {
        setModelAttribute(NAME, name);
        return this;
    }

    @Override
    public String getHost() {
        return getConfig(HOST);
    }

    @Override
    public V2CamelMqttBindingModel setHost(String host) {
        return setConfig(HOST, host);
    }

    @Override
    public String getLocalAddress() {
        return getConfig(LOCAL_ADDRESS);
    }

    @Override
    public V2CamelMqttBindingModel setLocalAddress(String localAddress) {
        return setConfig(LOCAL_ADDRESS, localAddress);
    }

    @Override
    public Integer getConnectAttemptsMax() {
        return getIntegerConfig(CONNECT_ATTEMPTS_MAX);
    }

    @Override
    public V2CamelMqttBindingModel setConnectAttemptsMax(int connectAttemptsMax) {
        return setConfig(CONNECT_ATTEMPTS_MAX, connectAttemptsMax);
    }

    @Override
    public Integer getReconnectAttemptsMax() {
        return getIntegerConfig(RECONNECT_ATTEMPTS_MAX);
    }

    @Override
    public V2CamelMqttBindingModel setReconnectAttemptsMax(int reconnectAttemptsMax) {
        return setConfig(RECONNECT_ATTEMPTS_MAX, reconnectAttemptsMax);
    }

    @Override
    public Integer getReconnectDelay() {
        return getIntegerConfig(RECONNECT_DELAY);
    }

    @Override
    public V2CamelMqttBindingModel setReconnectDelay(int reconnectDelay) {
        return setConfig(RECONNECT_DELAY, reconnectDelay);
    }

    @Override
    public Double getReconnectBackOffMultiplier() {
        return getDoubleConfig(RECONNECT_BACK_OFF_MULTIPLIER);
    }

    @Override
    public V2CamelMqttBindingModel setReconnectBackOffMultiplier(double reconnectBackOffMultiplier) {
        return setConfig(RECONNECT_BACK_OFF_MULTIPLIER, reconnectBackOffMultiplier);
    }

    @Override
    public Integer getReconnectDelayMax() {
        return getIntegerConfig(RECONNECT_DELAY_MAX);
    }

    @Override
    public V2CamelMqttBindingModel setReconnectDelayMax(int reconnectDelayMax) {
        return setConfig(RECONNECT_DELAY_MAX, reconnectDelayMax);
    }

    @Override
    public String getUserName() {
        return getConfig(USER_NAME);
    }

    @Override
    public V2CamelMqttBindingModel setUserName(String username) {
        return setConfig(USER_NAME, username);
    }

    @Override
    public String getPassword() {
        return getConfig(PASSWORD);
    }

    @Override
    public V2CamelMqttBindingModel setPassword(String password) {
        return setConfig(PASSWORD, password);
    }

    @Override
    public String getQualityOfService() {
        QualityOfService qualityOfService = getModelConfiguration() != null
                ? QualityOfService.valueOf(getConfig(QUALITY_OF_SERVICE))
                : QualityOfService.AtLeastOnce;
            return qualityOfService.name();
    }

    @Override
    public V2CamelMqttBindingModel setQualityOfService(String qualityOfService) {
        return setConfig(QUALITY_OF_SERVICE, qualityOfService);
    }

    @Override
    public String getSubscribeTopicName() {
        return getConfig(SUBSCRIBE_TOPIC_NAME);
    }

    @Override
    public V2CamelMqttBindingModel setSubscribeTopicName(String subscribeTopicName) {
        return setConfig(SUBSCRIBE_TOPIC_NAME, subscribeTopicName);
    }

    @Override
    public String getPublishTopicName() {
        return getConfig(PUBLISH_TOPIC_NAME);
    }

    @Override
    public V2CamelMqttBindingModel setPublishTopicName(String publishTopicName) {
        return setConfig(PUBLISH_TOPIC_NAME, publishTopicName);
    }

    @Override
    public Boolean isByDefaultRetain() {
        return getBooleanConfig(BY_DEFAULT_RETAIN);
    }

    @Override
    public V2CamelMqttBindingModel setByDefaultRetain(Boolean byDefaultRetain) {
        return setConfig(BY_DEFAULT_RETAIN, byDefaultRetain);
    }

    @Override
    public String getMqttTopicPropertyName() {
        return getConfig(MQTT_TOPIC_PROPERTY_NAME);
    }

    @Override
    public V2CamelMqttBindingModel setMqttTopicPropertyName(String mqttTopicPropertyName) {
        return setConfig(MQTT_TOPIC_PROPERTY_NAME, mqttTopicPropertyName);
    }

    @Override
    public String getMqttRetainPropertyName() {
        return getConfig(MQTT_RETAIN_PROPERTY_NAME);
    }

    @Override
    public V2CamelMqttBindingModel setMqttRetainPropertyName(String mqttRetainPropertyName) {
        return setConfig(MQTT_RETAIN_PROPERTY_NAME, mqttRetainPropertyName);
    }

    @Override
    public String getMqttQosPropertyName() {
        return getConfig(MQTT_QOS_PROPERTY_NAME);
    }

    @Override
    public V2CamelMqttBindingModel setMqttQosPropertyName(String mqttQosPropertyName) {
        return setConfig(MQTT_QOS_PROPERTY_NAME, mqttQosPropertyName);
    }

    @Override
    public Integer getConnectWaitInSeconds() {
        return getIntegerConfig(CONNECT_WAIT_IN_SECONDS);
    }

    @Override
    public V2CamelMqttBindingModel setConnectWaitInSeconds(int connectWaitInSeconds) {
        return setConfig(CONNECT_WAIT_IN_SECONDS, connectWaitInSeconds);
    }

    @Override
    public Integer getDisconnectWaitInSeconds() {
        return getIntegerConfig(DISCONNECT_WAIT_IN_SECONDS);
    }

    @Override
    public V2CamelMqttBindingModel setDisonnectWaitInSeconds(int disconnectWaitInSeconds) {
        return setConfig(DISCONNECT_WAIT_IN_SECONDS, disconnectWaitInSeconds);
    }

    @Override
    public Integer getSendWaitInSeconds() {
        return getIntegerConfig(SEND_WAIT_IN_SECONDS);
    }

    @Override
    public V2CamelMqttBindingModel setSendWaitInSeconds(int sendWaitInSeconds) {
        return setConfig(SEND_WAIT_IN_SECONDS, sendWaitInSeconds);
    }

    @Override
    public URI getComponentURI() {
        Configuration modelConfiguration = getModelConfiguration();
        List<Configuration> children = modelConfiguration.getChildren();

        String baseUri = MQTT + "://" + getName();

        QueryString queryStr = new QueryString();
        traverseConfiguration(children, queryStr, NAME);

        return URI.create(UnsafeUriCharactersEncoder.encode(baseUri + queryStr.toString()));
    }

}
