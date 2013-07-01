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
package org.switchyard.component.test.mixins;

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
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.jca.JCAMixIn;
import org.switchyard.component.test.mixins.naming.NamingMixIn;
import org.switchyard.component.test.mixins.transaction.TransactionMixIn;


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
