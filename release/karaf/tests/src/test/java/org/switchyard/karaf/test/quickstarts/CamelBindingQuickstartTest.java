package org.switchyard.karaf.test.quickstarts;

import org.junit.BeforeClass;
import org.junit.Test;

public class CamelBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.camel.file.binding";
    private static String featureName = "switchyard-quickstart-camel-file-binding";

    @BeforeClass
    public static void before() throws Exception {
        startTestContainer(featureName, bundleName, null, CamelBindingQuickstartProbe.class);
    }
    
    @Test
    public void testFeatures() throws Exception {
        executeProbe("testFeatures");
    }
}
