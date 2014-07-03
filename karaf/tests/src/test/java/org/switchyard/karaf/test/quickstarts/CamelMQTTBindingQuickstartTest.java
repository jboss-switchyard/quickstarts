package org.switchyard.karaf.test.quickstarts;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.replaceConfigurationFile;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.ops4j.pax.exam.CoreOptions;
import org.switchyard.common.type.Classes;

@Ignore("Disable for now due to https://issues.jboss.org/browse/SWITCHYARD-2220")
public class CamelMQTTBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard-camel-mqtt-binding";
    private static String featureName = "switchyard-quickstart-camel-mqtt-binding";

    private static final String TOPIC_INPUT = "camel/mqtt/test/input";
    private static final String TOPIC_OUTPUT = "camel/mqtt/test/output";
    private static final String MESSAGE_INPUT = "SquarePants";
    private static final String MESSAGE_OUTPUT = "Hello there " + MESSAGE_INPUT + " :-) ";
    private static final String USER_NAME = "karaf";
    private static final String PASSWORD = "karaf"; 

    @BeforeClass
    public static void before() throws Exception {
        URL activemqXml = Classes.getResource("activemq.xml");
        startTestContainer(featureName, bundleName,
                CoreOptions.options(
                        editConfigurationFilePut("etc/org.ops4j.pax.logging.cfg", "log4j.logger.org.apache.activemq",
                                "DEBUG"),
                                replaceConfigurationFile("etc/activemq.xml", new File(activemqXml.toURI()))), DeploymentProbe.class);
    }

    @Test
    public void testDeployment() throws Exception {
        BlockingConnection publishConnection = null;
        BlockingConnection subscribeConnection = null;
        try {
            Topic outputTopic = new Topic(TOPIC_OUTPUT, QoS.AT_LEAST_ONCE);
            MQTT mqtt = new MQTT();
            mqtt.setUserName(USER_NAME);
            mqtt.setPassword(PASSWORD);
            subscribeConnection = mqtt.blockingConnection();
            subscribeConnection.connect();
            subscribeConnection.subscribe(new Topic[]{outputTopic});

            publishConnection = mqtt.blockingConnection();
            publishConnection.connect();
            publishConnection.publish(TOPIC_INPUT, MESSAGE_INPUT.getBytes(), QoS.AT_LEAST_ONCE, false);

            Message message = subscribeConnection.receive(1000, TimeUnit.MILLISECONDS);
            Assert.assertNotNull("No output message from " + TOPIC_OUTPUT, message);
            Assert.assertEquals(MESSAGE_OUTPUT, new String(message.getPayload()));
            Assert.assertNull("More than one message received from " + TOPIC_OUTPUT,
                    subscribeConnection.receive(1000, TimeUnit.MILLISECONDS));
        } finally {
            if (publishConnection != null) {
                publishConnection.disconnect();
            }
        }
    }
}
