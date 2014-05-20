package org.switchyard.karaf.test.quickstarts;

import org.junit.Before;

public class CamelAtomBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard-camel-atom-binding";
    private static String featureName = "switchyard-quickstart-camel-atom-binding";

    @Before
    public void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }
}
