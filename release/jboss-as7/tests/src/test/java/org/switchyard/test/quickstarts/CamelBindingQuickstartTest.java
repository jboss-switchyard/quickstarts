/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.test.quickstarts;

import java.io.File;
import java.io.FileWriter;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;

/**
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
@RunWith(Arquillian.class)
public class CamelBindingQuickstartTest {

    private static String DEST_FILE = "target/input/test.txt";

    @Deployment(testable = true)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-camel-file-binding");
    }

    @Test
    public void testDeployment() throws Exception {
        File destFile = new File(DEST_FILE);
        destFile.getParentFile().mkdirs();
        FileWriter writer = null;
        try {
            writer = new FileWriter(destFile);
            writer.append("Captain Crunch");
            Assert.assertTrue(destFile.exists());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        // Wait a spell so that the file component polls and picks up the file
        while (destFile.exists()) {
            Thread.sleep(50);
        }
        // File should have been picked up
        Assert.assertFalse(destFile.exists());
    }

}
