package org.switchyard.karaf.test.quickstarts;

import org.junit.Before;
import org.junit.Test;

public class CamelBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard-camel-binding";
    private static String featureName = "switchyard-quickstart-camel-binding";

    @Before
    public void before() throws Exception {
        startTestContainer(featureName, bundleName);
    }
    
    @Test
    public void testFeatures() throws Exception {
        executeProbe(CamelBindingQuickstartProbe.class, "testFeatures");
    }
}
