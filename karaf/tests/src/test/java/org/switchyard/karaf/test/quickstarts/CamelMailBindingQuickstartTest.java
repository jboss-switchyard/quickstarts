package org.switchyard.karaf.test.quickstarts;

import org.junit.Before;

public class CamelMailBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard-camel-mail-binding";
    private static String featureName = "switchyard-quickstart-camel-mail-binding";

    @Before
    public void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }
}
