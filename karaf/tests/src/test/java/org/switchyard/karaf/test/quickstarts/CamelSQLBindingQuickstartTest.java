package org.switchyard.karaf.test.quickstarts;

import org.junit.BeforeClass;
import org.ops4j.pax.exam.CoreOptions;

public class CamelSQLBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.camel.sql.binding";
    private static String featureName = "switchyard-quickstart-camel-sql-binding";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName, CoreOptions.options(CoreOptions.systemProperty(
                "org.switchyard.quickstarts.camel.sql.datasourceName").value("osgi:service/jboss/myDS")),
                DeploymentProbe.class);
    }
}
