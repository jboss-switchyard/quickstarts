package org.switchyard.component.camel.mqtt.model.v2;

import static org.junit.Assert.assertEquals;

import org.apache.camel.component.mqtt.MQTTEndpoint;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.component.camel.mqtt.model.CamelMqttNamespace;
import org.switchyard.component.camel.mqtt.model.v2.V2CamelMqttBindingModel.QualityOfService;

public class V2CamelMqttBindingModelTest extends V1BaseCamelServiceBindingModelTest<V2CamelMqttBindingModel, MQTTEndpoint> {

    private static final String NAME = "Garfield";
    private static final String HOST = "tcp://127.0.0.1:1883";
    private static final String LOCAL_ADDRESS = "tcp://127.0.0.1:1883";
    private static final Integer CONNECT_ATTEMPTS_MAX = 10;
    private static final Integer RECONNECT_ATTEMPTS_MAX = 10;
    private static final Integer RECONNECT_DELAY = 10;
    private static final Double RECONNECT_BACK_OFF_MULTIPLIER = 5.0;
    private static final Integer RECONNECT_DELAY_MAX = 100;
    private static final String USER_NAME = "karaf";
    private static final String PASSWORD = "karaf";
    private static final String QUALITY_OF_SERVICE = QualityOfService.ExactlyOnce.name();
    private static final String SUBSCRIBE_TOPIC_NAME = "camel/mqtt/test";
    private static final String PUBLISH_TOPIC_NAME = "camel/mqtt/test";
    private static final Boolean BY_DEFAULT_RETAIN = true;
    private static final String MQTT_TOPIC_PROPERTY_NAME = "MQTT_TOPIC";
    private static final String MQTT_RETAIN_PROPERTY_NAME = "MQTT_RETAIN";
    private static final String MQTT_QOS_PROPERTY_NAME = "MQTT_QOS";
    private static final Integer CONNECT_WAIT_IN_SECONDS = 10;
    private static final Integer DISCONNECT_WAIT_IN_SECONDS = 10;
    private static final Integer SEND_WAIT_IN_SECONDS = 10;

    private static final String CAMEL_XML = "/v2/switchyard-mqtt-binding-beans.xml";
    private static final String CAMEL_URI = "mqtt://" + NAME + "?host=" + HOST + "&localAddress=" + LOCAL_ADDRESS + "&connectAttemptsMax=" + CONNECT_ATTEMPTS_MAX 
            + "&reconnectAttemptsMax=" + RECONNECT_ATTEMPTS_MAX + "&reconnectDelay=" + RECONNECT_DELAY + "&reconnectBackOffMultiplier=" + RECONNECT_BACK_OFF_MULTIPLIER 
            + "&reconnectDelayMax=" + RECONNECT_DELAY_MAX + "&userName=" + USER_NAME + "&password=" + PASSWORD + "&qualityOfService=" + QUALITY_OF_SERVICE
            + "&subscribeTopicName=" + SUBSCRIBE_TOPIC_NAME + "&publishTopicName=" + PUBLISH_TOPIC_NAME + "&byDefaultRetain=" + BY_DEFAULT_RETAIN
            + "&mqttTopicPropertyName=" + MQTT_TOPIC_PROPERTY_NAME + "&mqttRetainPropertyName=" + MQTT_RETAIN_PROPERTY_NAME
            + "&mqttQosPropertyName=" + MQTT_QOS_PROPERTY_NAME + "&connectWaitInSeconds=" + CONNECT_WAIT_IN_SECONDS
            + "&disconnectWaitInSeconds=" + DISCONNECT_WAIT_IN_SECONDS + "&sendWaitInSeconds=" + SEND_WAIT_IN_SECONDS;

    public V2CamelMqttBindingModelTest () {
        super(MQTTEndpoint.class, CAMEL_XML);
        setSkipCamelEndpointTesting(true);
    }
    
    @Override
    protected void createModelAssertions(V2CamelMqttBindingModel model) {
        assertEquals(NAME, model.getName());
        assertEquals(HOST, model.getHost());
        assertEquals(LOCAL_ADDRESS, model.getLocalAddress());
        assertEquals(CONNECT_ATTEMPTS_MAX, model.getConnectAttemptsMax());
        assertEquals(RECONNECT_ATTEMPTS_MAX, model.getReconnectAttemptsMax());
        assertEquals(RECONNECT_DELAY, model.getReconnectDelay());
        assertEquals(RECONNECT_BACK_OFF_MULTIPLIER, model.getReconnectBackOffMultiplier());
        assertEquals(RECONNECT_DELAY_MAX, model.getReconnectDelayMax());
        assertEquals(USER_NAME, model.getUserName());
        assertEquals(PASSWORD, model.getPassword());
        assertEquals(QUALITY_OF_SERVICE, model.getQualityOfService());
        assertEquals(SUBSCRIBE_TOPIC_NAME, model.getSubscribeTopicName());
        assertEquals(PUBLISH_TOPIC_NAME, model.getPublishTopicName());
        assertEquals(BY_DEFAULT_RETAIN, model.isByDefaultRetain());
        assertEquals(MQTT_TOPIC_PROPERTY_NAME, model.getMqttTopicPropertyName());
        assertEquals(MQTT_RETAIN_PROPERTY_NAME, model.getMqttRetainPropertyName());
        assertEquals(MQTT_QOS_PROPERTY_NAME, model.getMqttQosPropertyName());
        assertEquals(CONNECT_WAIT_IN_SECONDS, model.getConnectWaitInSeconds());
        assertEquals(DISCONNECT_WAIT_IN_SECONDS, model.getDisconnectWaitInSeconds());
        assertEquals(SEND_WAIT_IN_SECONDS, model.getSendWaitInSeconds());
    }

    @Override
    protected V2CamelMqttBindingModel createTestModel() {
        return new V2CamelMqttBindingModel(CamelMqttNamespace.V_2_0.uri())
            .setName(NAME)
            .setHost(HOST)
            .setLocalAddress(LOCAL_ADDRESS)
            .setConnectAttemptsMax(CONNECT_ATTEMPTS_MAX)
            .setReconnectAttemptsMax(RECONNECT_ATTEMPTS_MAX)
            .setReconnectDelay(RECONNECT_DELAY)
            .setReconnectBackOffMultiplier(RECONNECT_BACK_OFF_MULTIPLIER)
            .setReconnectDelayMax(RECONNECT_DELAY_MAX)
            .setUserName(USER_NAME)
            .setPassword(PASSWORD)
            .setQualityOfService(QUALITY_OF_SERVICE)
            .setSubscribeTopicName(SUBSCRIBE_TOPIC_NAME)
            .setPublishTopicName(PUBLISH_TOPIC_NAME)
            .setByDefaultRetain(BY_DEFAULT_RETAIN)
            .setMqttTopicPropertyName(MQTT_TOPIC_PROPERTY_NAME)
            .setMqttRetainPropertyName(MQTT_RETAIN_PROPERTY_NAME)
            .setMqttQosPropertyName(MQTT_QOS_PROPERTY_NAME)
            .setConnectWaitInSeconds(CONNECT_WAIT_IN_SECONDS)
            .setDisonnectWaitInSeconds(DISCONNECT_WAIT_IN_SECONDS)
            .setSendWaitInSeconds(SEND_WAIT_IN_SECONDS);
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }

}
