/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.test.mixins;

import java.util.Arrays;
import java.util.HashSet;

import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.TestMixIn;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.naming.NamingMixIn;
import org.switchyard.component.test.mixins.transaction.TransactionMixIn;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit test which checks usage of {@link CDIMixIn} together with {@link TransactionMixIn}.
 */
public class CDIMixInWithTransaction {

    private static TransactionMixIn transactionMixIn;
    private static NamingMixIn namingMixIn;
    private static CDIMixIn cdiMixIn;

    @BeforeClass
    public static void setup() {
        namingMixIn = new NamingMixIn();
        namingMixIn.initialize();
        transactionMixIn = new TransactionMixIn();
        transactionMixIn.initialize();
        cdiMixIn = new CDIMixIn();

        cdiMixIn.setTestKit(createMockTestKit());
        cdiMixIn.initialize();
    }

    /**
     * Creates mock to catch TestKit calls.
     */
    private static SwitchYardTestKit createMockTestKit() {
        HashSet<TestMixIn> mixins = new HashSet<TestMixIn>(Arrays.<TestMixIn>asList(transactionMixIn));
        SwitchYardTestKit testKitMock = mock(SwitchYardTestKit.class);
        when(testKitMock.getOptionalDependencies(any(CDIMixIn.class))).thenReturn(mixins);
        return testKitMock;
    }

    @AfterClass
    public static void tearDown() {
        verify(cdiMixIn.getTestKit());
        cdiMixIn.uninitialize();
        transactionMixIn.uninitialize();
        namingMixIn.uninitialize();
    }

    @Test
    public void shouldUseSameInstance() throws Exception {
        assertNotNull(getUserTransaction());
    }

    private UserTransaction getUserTransaction() throws NamingException {
        return cdiMixIn.getObject(UserTransaction.class);
    }

}
