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

package org.switchyard.component.bean.tests;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;

/*
 * Assorted methods for testing a CDI bean consuming a service in SwitchYard.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "BeanPropertyTests.xml", mixins = CDIMixIn.class)
public class BeanPropertyTest {

    @ServiceOperation("PropertyService.getProperties")
    private Invoker _invoker;

    @Test
    public void testBeanProperty() {
        Map<String,String> response = _invoker.sendInOut(null).getContent(Map.class);
        Assert.assertEquals("bar", response.get("foo"));
        Assert.assertEquals("composite.bar", response.get("composite.foo"));
        Assert.assertEquals("component.bar", response.get("component.foo"));
    }
}
