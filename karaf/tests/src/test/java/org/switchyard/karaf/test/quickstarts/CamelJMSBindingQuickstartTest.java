package org.switchyard.karaf.test.quickstarts;

import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;
import org.switchyard.test.quickstarts.util.ResourceDeployer;

public class CamelJMSBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard-camel-jms-binding";
    private static String featureName = "switchyard-quickstart-camel-jms-binding";

    private static final String QUEUE = "GreetingServiceQueue";

    @Before
    public void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }

    //Quickstart requires hornetq standalone to be running
    @Ignore @Test
    public void testDeployment() throws Exception {
        HornetQMixIn hqMixIn = new HornetQMixIn(false)
                                    .setUser(ResourceDeployer.USER)
                                    .setPassword(ResourceDeployer.PASSWD);
        hqMixIn.initialize();

        try {
            Session session = hqMixIn.getJMSSession();
            MessageProducer producer = session.createProducer(HornetQMixIn.getJMSQueue(QUEUE));
            Message message = hqMixIn.createJMSMessage("Tomo");
            producer.send(message);
        } finally {
            hqMixIn.uninitialize();
            ResourceDeployer.removeQueue(QUEUE);
        }
    }
}
