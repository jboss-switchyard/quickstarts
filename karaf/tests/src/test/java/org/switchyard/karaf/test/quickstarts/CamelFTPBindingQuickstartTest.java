package org.switchyard.karaf.test.quickstarts;

import org.junit.Before;

public class CamelFTPBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard-camel-ftp-binding";
    private static String featureName = "switchyard-quickstart-camel-ftp-binding";

    @Before
    public void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }
}
