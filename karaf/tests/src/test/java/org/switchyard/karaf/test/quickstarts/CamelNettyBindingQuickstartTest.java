package org.switchyard.karaf.test.quickstarts;

import org.junit.Before;

public class CamelNettyBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard-camel-netty-binding";
    private static String featureName = "switchyard-quickstart-camel-netty-binding";

    @Before
    public void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }
}
