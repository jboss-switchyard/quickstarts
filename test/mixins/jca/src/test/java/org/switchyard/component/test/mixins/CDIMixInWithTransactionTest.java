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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.junit.Assert;
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
public class CDIMixInWithTransactionTest {

    private static TransactionMixIn transactionMixIn;
    private static NamingMixIn namingMixIn;
    private static CDIMixIn cdiMixIn;
    private static MyTransactionMixInParticipant myTxMixInParticipant;
    private static boolean _transactionMixInParticipantInvoked = false;

    @BeforeClass
    public static void setup() {
        namingMixIn = new NamingMixIn();
        transactionMixIn = new TransactionMixIn();
        cdiMixIn = new CDIMixIn();
        myTxMixInParticipant = new MyTransactionMixInParticipant(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                _transactionMixInParticipantInvoked = true;
                return null;
            }
        });
        List<TestMixIn> mixins = new ArrayList<TestMixIn>(Arrays.<TestMixIn>asList(namingMixIn,transactionMixIn,cdiMixIn,myTxMixInParticipant));
        SwitchYardTestKit testkit = mock(SwitchYardTestKit.class);
        when(testkit.getMixIns()).thenReturn(mixins);
        namingMixIn.setTestKit(testkit);
        transactionMixIn.setTestKit(testkit);
        cdiMixIn.setTestKit(testkit);
        myTxMixInParticipant.setTestKit(testkit);
        namingMixIn.initialize();
        transactionMixIn.initialize();
        cdiMixIn.initialize();
        myTxMixInParticipant.initialize();
    }

    @AfterClass
    public static void tearDown() {
        myTxMixInParticipant.uninitialize();
        cdiMixIn.uninitialize();
        transactionMixIn.uninitialize();
        namingMixIn.uninitialize();
    }

    @Test
    public void testUserTransaction() throws Exception {
        assertNotNull(getUserTransaction());
    }

    @Test
    public void testTransactionMixInParticipant() {
        Assert.assertTrue(_transactionMixInParticipantInvoked);
    }

    private UserTransaction getUserTransaction() throws NamingException {
        return cdiMixIn.getObject(UserTransaction.class);
    }

}
