package org.switchyard.karaf.test.quickstarts;

import org.junit.Before;

public class CamelAMQPBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard-camel-amqp-binding";
    private static String featureName = "switchyard-quickstart-camel-amqp-binding";

    @Before
    public void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }
}
