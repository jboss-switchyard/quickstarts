package org.switchyard.karaf.test.quickstarts;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ops4j.pax.exam.CoreOptions;

public class CamelJMSBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.camel.jms.binding";
    private static String featureName = "switchyard-quickstart-camel-jms-binding";

    private static final String AMQ_USER = "karaf";
    private static final String AMQ_PASSWD = "karaf";
    private static final String AMQ_BROKER_URL = "tcp://localhost:61616";

    private static final String QUEUE_NAME = "GreetingServiceQueue";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName,
                CoreOptions.options(
                        editConfigurationFilePut("etc/org.ops4j.pax.logging.cfg", "log4j.logger.org.apache.activemq",
                                "DEBUG")), DeploymentProbe.class);
    }

    @Test
    public void testDeployment() throws Exception {
        ConnectionFactory cf = new ActiveMQConnectionFactory(AMQ_USER, AMQ_PASSWD, AMQ_BROKER_URL);
        Connection conn = cf.createConnection();

        try {
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            final MessageProducer producer = session.createProducer(session.createQueue(QUEUE_NAME));
            Message message = session.createTextMessage("Captain Crunch");
            producer.send(message);
        } finally {
            conn.close();
        }
    }
}
