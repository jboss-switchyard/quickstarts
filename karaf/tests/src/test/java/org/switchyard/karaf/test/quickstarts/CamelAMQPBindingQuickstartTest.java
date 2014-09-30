package org.switchyard.karaf.test.quickstarts;

import org.junit.BeforeClass;

public class CamelAMQPBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.camel.amqp.binding";
    private static String featureName = "switchyard-quickstart-camel-amqp-binding";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }
}
