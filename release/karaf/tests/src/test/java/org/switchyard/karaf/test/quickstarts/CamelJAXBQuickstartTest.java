package org.switchyard.karaf.test.quickstarts;

import org.junit.BeforeClass;

public class CamelJAXBQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.camel.jaxb";
    private static String featureName = "switchyard-quickstart-camel-jaxb";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }
}
