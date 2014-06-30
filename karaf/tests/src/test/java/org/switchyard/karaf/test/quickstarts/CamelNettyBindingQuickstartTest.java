package org.switchyard.karaf.test.quickstarts;

import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * Requires security framework.
 */
@Ignore
public class CamelNettyBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard-camel-netty-binding";
    private static String featureName = "switchyard-quickstart-camel-netty-binding";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }
}
