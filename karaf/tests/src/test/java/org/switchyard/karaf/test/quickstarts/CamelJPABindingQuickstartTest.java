package org.switchyard.karaf.test.quickstarts;

import org.junit.Before;

public class CamelJPABindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard-camel-jpa-binding";
    private static String featureName = "switchyard-quickstart-camel-jpa-binding";

    @Before
    public void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }
}
