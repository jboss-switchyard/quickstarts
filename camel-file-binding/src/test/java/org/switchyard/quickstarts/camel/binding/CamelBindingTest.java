/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.camel.binding;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = SwitchYardTestCaseConfig.SWITCHYARD_XML, mixins = CDIMixIn.class)
public class CamelBindingTest {

    private static String SOURCE_FILE = "target/test-classes/test.txt";
    private static String DEST_FILE = "target/input/test.txt";

    @Test
    public void testCamelFileBinding() throws Exception {
        // Move our test file into position
        File srcFile = new File(SOURCE_FILE);
        File destFile = new File(DEST_FILE);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        srcFile.renameTo(destFile);

        assertTrue(destFile.exists());
        // Wait a spell so that the file component polls and picks up the file

        // File should have been picked up and output displayed via the console
        while (destFile.exists()) {
            Thread.sleep(50);
        }
        assertFalse(destFile.exists());
    }
}
