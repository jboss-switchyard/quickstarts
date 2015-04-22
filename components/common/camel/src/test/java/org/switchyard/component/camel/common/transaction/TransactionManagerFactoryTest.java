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
package org.switchyard.component.camel.common.transaction;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.switchyard.SwitchYardException;
import org.switchyard.component.test.mixins.naming.NamingMixIn;

/**
 * Unit test for {@link TransactionManagerFactory}.
 * 
 * @author Daniel Bevenius
 */
public class TransactionManagerFactoryTest {

    private NamingMixIn mixIn;

    @Before
    public void setUp() {
        mixIn = new NamingMixIn();
        mixIn.initialize();
    }

    @After
    public void shutDown() {
        mixIn.uninitialize();
    }

    @Test
    public void getInstance() {
        final TransactionManagerFactory factory = TransactionManagerFactory.getInstance();
        assertThat(factory, is(notNullValue()));
    }

    @Test
    public void createSpringTransactionManager() throws Exception {
        mixIn.getInitialContext().bind(TransactionManagerFactory.JBOSS_USER_TRANSACTION, mock(UserTransaction.class));
        mixIn.getInitialContext().bind(TransactionManagerFactory.JBOSS_TRANSACTION_MANANGER, mock(TransactionManager.class));
        mixIn.getInitialContext().bind(TransactionManagerFactory.JBOSS_TRANSACTION_SYNC_REG, mock(TransactionSynchronizationRegistry.class));
        final TransactionManagerFactory factory = TransactionManagerFactory.getInstance();
        final PlatformTransactionManager tm = factory.create();
        assertThat(tm, is(notNullValue()));
    }

    @Test (expected = SwitchYardException.class)
    public void showThrowIfTMIsUnknown() {
        final TransactionManagerFactory factory = TransactionManagerFactory.getInstance();
        factory.create();
    }

    @Test
    public void createJBossTransactionManager() throws Exception {
        mixIn.getInitialContext().bind(TransactionManagerFactory.JBOSS_USER_TRANSACTION, mock(UserTransaction.class));
        mixIn.getInitialContext().bind(TransactionManagerFactory.JBOSS_TRANSACTION_MANANGER, mock(TransactionManager.class));
        mixIn.getInitialContext().bind(TransactionManagerFactory.JBOSS_TRANSACTION_SYNC_REG, mock(TransactionSynchronizationRegistry.class));
        final TransactionManagerFactory factory = TransactionManagerFactory.getInstance();
        final PlatformTransactionManager tm = factory.create();
        assertThat(tm, is(instanceOf(JtaTransactionManager.class)));
    }

}
