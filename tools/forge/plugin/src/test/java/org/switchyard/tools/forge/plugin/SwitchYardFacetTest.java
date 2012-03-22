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

package org.switchyard.tools.forge.plugin;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for SwitchYardPluginTest
 */
public class SwitchYardFacetTest {

    static final String ORIG_CONFIG_PATH = "target/test-classes/standalone.xml";
    static final String BACKUP_CONFIG_PATH = ORIG_CONFIG_PATH + ".orig";
    
    @Test
    public void testVersionCheck() throws Exception {
        SwitchYardFacet facet = new SwitchYardFacet();
        // test postive case
        String version = facet.loadSwitchYardVersion(SwitchYardFacet.PROPS_PATH);
        System.out.println(version);
        Assert.assertNotNull(version);
        
        // test negative case
        Assert.assertNull(facet.loadSwitchYardVersion("foo/doesntexist.properties"));
    }
    
    @Test
    public void testSwitchYardTransform() throws Exception {
        SwitchYardFacet facet = new SwitchYardFacet();
        facet.addSwitchYardToASConfig(ORIG_CONFIG_PATH);
        Assert.assertTrue(new File(ORIG_CONFIG_PATH).exists());
        Assert.assertTrue(new File(BACKUP_CONFIG_PATH).exists());
    }
}
