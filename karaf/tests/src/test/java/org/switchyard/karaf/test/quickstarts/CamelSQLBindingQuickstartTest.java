package org.switchyard.karaf.test.quickstarts;

import org.junit.Before;

public class CamelSQLBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard-camel-sql-binding";
    private static String featureName = "switchyard-quickstart-camel-sql-binding";

    @Before
    public void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }
}
