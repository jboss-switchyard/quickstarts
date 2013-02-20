/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
