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
package org.switchyard.test.mixins;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;

import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.TestMixIn;
import org.switchyard.test.mixins.jca.JCAMixIn;

/**
 * Unit test which checks usage of {@link CDIMixIn} together with {@link TransactionMixIn}.
 */
public class CDIMixInWithJCATransaction {

    private static TransactionMixIn transactionMixIn;
    private static NamingMixIn namingMixIn;
    private static CDIMixIn cdiMixIn;
	private static JCAMixIn jcaMixIn;

    @BeforeClass
    public static void setup() {
        namingMixIn = new NamingMixIn();
        namingMixIn.initialize();

        jcaMixIn = new JCAMixIn();
        jcaMixIn.initialize();

        transactionMixIn = new TransactionMixIn();
        transactionMixIn.setTestKit(createMockTestKit(TransactionMixIn.class, jcaMixIn));
        transactionMixIn.initialize();

        cdiMixIn = new CDIMixIn();
        cdiMixIn.setTestKit(createMockTestKit(CDIMixIn.class, transactionMixIn));
        cdiMixIn.initialize();
    }

    /**
     * Creates mock to catch TestKit calls.
     * 
     * @param type Type of mixin which asks for dependencies.
     * @param mixIns dependencies to be returned.
     */
    private static SwitchYardTestKit createMockTestKit(Class<? extends TestMixIn> type, TestMixIn ... mixIns) {
        HashSet<TestMixIn> mixins = new HashSet<TestMixIn>(Arrays.<TestMixIn>asList(mixIns));
        SwitchYardTestKit testKitMock = mock(SwitchYardTestKit.class);
        when(testKitMock.getOptionalDependencies(any(type))).thenReturn(mixins);
        return testKitMock;
    }

    @AfterClass
    public static void tearDown() {
        verify(transactionMixIn.getTestKit()).getOptionalDependencies(Matchers.<TestMixIn>anyObject());
        verify(cdiMixIn.getTestKit()).getOptionalDependencies(Matchers.<TestMixIn>anyObject());
        cdiMixIn.uninitialize();
        jcaMixIn.uninitialize();
        transactionMixIn.uninitialize();
        namingMixIn.uninitialize();
    }

    @Test
    public void shouldUseSameInstance() throws Throwable {
        assertNotNull(getUserTransaction());
        assertSame(jcaMixIn.locateEnvironmentBean(), transactionMixIn.getEnvironmentBean());
    }

    private UserTransaction getUserTransaction() throws NamingException {
        return cdiMixIn.getObject(UserTransaction.class);
    }

}
