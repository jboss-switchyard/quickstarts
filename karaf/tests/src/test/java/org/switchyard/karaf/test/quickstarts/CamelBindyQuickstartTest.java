package org.switchyard.karaf.test.quickstarts;

import org.junit.Before;

public class CamelBindyQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard-camel-bindy";
    private static String featureName = "switchyard-quickstart-camel-bindy";

    @Before
    public void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }
}
