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
package org.switchyard.component.camel.transaction;

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
import org.switchyard.exception.SwitchYardException;
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
        mixIn.getInitialContext().bind("java:comp/UserTransaction", mock(UserTransaction.class));
        mixIn.getInitialContext().bind("java:comp/TransactionManager", mock(TransactionManager.class));
        mixIn.getInitialContext().bind("java:comp/TransactionSynchronizationRegistry", mock(TransactionSynchronizationRegistry.class));
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
        mixIn.getInitialContext().bind("java:jboss/UserTransaction", mock(UserTransaction.class));
        mixIn.getInitialContext().bind("java:jboss/TransactionManager", mock(TransactionManager.class));
        mixIn.getInitialContext().bind("java:jboss/TransactionSynchronizationRegistry", mock(TransactionSynchronizationRegistry.class));
        final TransactionManagerFactory factory = TransactionManagerFactory.getInstance();
        final PlatformTransactionManager tm = factory.create();
        assertThat(tm, is(instanceOf(JtaTransactionManager.class)));
    }

}
