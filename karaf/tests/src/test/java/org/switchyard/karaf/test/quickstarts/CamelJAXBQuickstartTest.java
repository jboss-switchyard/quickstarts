package org.switchyard.karaf.test.quickstarts;

import org.junit.Before;

public class CamelJAXBQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard-camel-jaxb";
    private static String featureName = "switchyard-quickstart-camel-jaxb";

    @Before
    public void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }
}
