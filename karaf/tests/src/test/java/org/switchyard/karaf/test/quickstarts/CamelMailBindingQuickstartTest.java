package org.switchyard.karaf.test.quickstarts;

import org.junit.BeforeClass;

public class CamelMailBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.camel.mail.binding";
    private static String featureName = "switchyard-quickstart-camel-mail-binding";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }
}
