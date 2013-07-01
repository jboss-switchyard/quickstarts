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

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.switchyard.component.test.mixins.naming.NamingMixIn;
import org.switchyard.component.test.mixins.transaction.TransactionMixIn;

import static org.junit.Assert.assertSame;

/**
 * Unit test for {@link TransactionMixIn}.
 * 
 * @author Lukasz Dywicki
 */
public class TransactionMixInTest {

    private static TransactionMixIn transactionMixIn;
    private static NamingMixIn namingMixIn;

    @BeforeClass
    public static void setup() {
        namingMixIn = new NamingMixIn();
        namingMixIn.initialize();
        transactionMixIn = new TransactionMixIn();
        transactionMixIn.initialize();
    }
    
    @AfterClass
    public static void tearDown() {
        transactionMixIn.uninitialize();
    }

    @Test
    public void testTransaction() throws Exception {
        UserTransaction transaction = getUserTransaction();
        transaction.begin();

        transaction.commit();
    }

    @Ignore // Fails with EAP6
    @Test
    public void shouldUseSameInstance() throws Exception {
        assertSame(getUserTransaction(), transactionMixIn.getUserTransaction());
    }

    private UserTransaction getUserTransaction() throws NamingException {
        return (UserTransaction) new InitialContext().lookup("java:jboss/UserTransaction");
    }

}
