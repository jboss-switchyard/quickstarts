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
package org.switchyard.tools.forge.plugin;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for SwitchYardPluginTest.
 */
public class SwitchYardFacetTest {

    private static final String ORIG_CONFIG_PATH = "target/test-classes/standalone.xml";
    private static final String BACKUP_CONFIG_PATH = ORIG_CONFIG_PATH + ".orig";

    @Test
    public void testSwitchYardTransform() throws Exception {
        SwitchYardFacet facet = new SwitchYardFacet();
        facet.addSwitchYardToASConfig(ORIG_CONFIG_PATH);
        Assert.assertTrue(new File(ORIG_CONFIG_PATH).exists());
        Assert.assertTrue(new File(BACKUP_CONFIG_PATH).exists());
    }

    @After
    public void after() throws Exception {
        File backupConfigFile = new File(BACKUP_CONFIG_PATH);
        if (backupConfigFile.exists()) {
            backupConfigFile.delete();
        }
    }

}
