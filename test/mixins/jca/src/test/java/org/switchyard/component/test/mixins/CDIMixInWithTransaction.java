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
