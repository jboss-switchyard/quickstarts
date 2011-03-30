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

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.Message;
import org.switchyard.test.SwitchYardTestCase;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;

/*
 * Assorted methods for testing a CDI bean providing a service in SwitchYard.
 */
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class)
public class BeanProviderTest extends SwitchYardTestCase {

    @Test
    public void invokeOneWayProviderWithInOnly() {
        newInvoker("OneWay.oneWay").sendInOnly("hello");
    }
    
    @Test
    public void invokeRequestResponseProviderWithInOut() {
        String ECHO_MSG = "hello";
        Message response = newInvoker("RequestResponse.reply").sendInOut(ECHO_MSG);

        Assert.assertEquals(ECHO_MSG, response.getContent());
    }
}
