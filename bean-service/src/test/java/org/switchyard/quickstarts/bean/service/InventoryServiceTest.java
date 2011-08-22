/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2010-2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
 * details. You should have received a copy of the GNU Lesser General Public 
 * License, v.2.1 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301, USA.
 */

package org.switchyard.quickstarts.bean.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.InvocationFaultException;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class)
public class InventoryServiceTest {

    @ServiceOperation("InventoryService.lookupItem")
    private Invoker lookupItem;

    @Test
    public void testItemLookupExists() throws Exception {
        final String ITEM_ID = "BUTTER";
        Item item = lookupItem
            .sendInOut(ITEM_ID)
            .getContent(Item.class);

        Assert.assertNotNull(item);
        Assert.assertEquals(ITEM_ID, item.getItemId());
    }
    
    @Test
    public void testItemLookupNonexistent() throws Exception {
        final String ITEM_ID = "GUNS";
        try {
            // This should generate a fault because the ITEM_ID is not found
            lookupItem
                .sendInOut(ITEM_ID)
                .getContent(Item.class);
            // Looks like we didn't fault, so fail
            Assert.fail("Invalid itemId accepted: " + ITEM_ID);
        } catch (InvocationFaultException ifEx) {
            Assert.assertTrue(ifEx.isType(ItemNotFoundException.class));
        } 
    }
}
