package org.switchyard.karaf.test.quickstarts;

import org.junit.BeforeClass;

public class CamelFTPBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.camel.ftp.binding";
    private static String featureName = "switchyard-quickstart-camel-ftp-binding";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }
}
