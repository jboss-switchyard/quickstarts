package org.switchyard.karaf.test.quickstarts;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.switchyard.common.io.Files;

public class CamelBindingQuickstartProbe extends DeploymentProbe {

    private static String SOURCE_FILE = "../../../test-classes/quickstarts/camel-binding/test.txt";
    private static String DEST_FILE = "target/input/test.txt";

    public CamelBindingQuickstartProbe() {
    }
    
    @Test
    public void testFeatures() throws Exception {
        File srcFile = new File(SOURCE_FILE);
        assertTrue(srcFile.exists());
        File destFile = new File(DEST_FILE);
        Files.copy(srcFile, destFile);
        assertTrue(destFile.exists());
        // Wait a spell so that the file component polls and picks up the file
        while (destFile.exists()) {
            Thread.sleep(50);
        }
        // File should have been picked up
        assertFalse(destFile.exists());
    }
}
