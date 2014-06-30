package org.switchyard.karaf.test.quickstarts;

import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * JPA quickstart needs to be built specifically for Karaf. The problem lies
 * with the JNDI name used to lookup the datasource in the persistence.xml.
 */
@Ignore
public class CamelJPABindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard-camel-jpa-binding";
    private static String featureName = "switchyard-quickstart-camel-jpa-binding";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }
}
