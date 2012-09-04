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

package org.switchyard.handlers;

import java.util.UUID;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.MockExchange;
import org.switchyard.policy.PolicyUtil;
import org.switchyard.policy.TransactionPolicy;

public class TransactionHandlerTest {

    private MockTransactionManager tm;
    private MockExchange exchange;
    private TransactionHandler handler;

    @Before
    public void setUp() {
        tm = new MockTransactionManager();
        exchange = new MockExchange();
        handler = new TransactionHandler();
        handler.setTransactionManager(tm);
    }

    /* policy combination error */
    
    @Test
    public void incompatibleRequirements_PropagatesAndSuspends() {
        PolicyUtil.require(exchange, TransactionPolicy.PROPAGATES_TRANSACTION);
        PolicyUtil.require(exchange, TransactionPolicy.SUSPENDS_TRANSACTION);
        exchange.setPhase(ExchangePhase.IN);

        try {
            handler.handleMessage(exchange);
        } catch (HandlerException handlerEx) {
            // expected
            System.out.println(handlerEx.toString());
            return;
        }

        Assert.fail("Expected a handler exception due to incompatible policy");
    }

    @Test
    public void incompatibleRequirements_managedGlobalAndmanagedLocal() {
        PolicyUtil.require(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL);
        PolicyUtil.require(exchange, TransactionPolicy.MANAGED_TRANSACTION_LOCAL);
        exchange.setPhase(ExchangePhase.IN);

        try {
            handler.handleMessage(exchange);
        } catch (HandlerException handlerEx) {
            System.out.println(handlerEx.toString());
            return;
        }

        Assert.fail("Expected a handler exception due to incompatible policy");
    }

    @Test
    public void incompatibleRequirements_managedGlobalAndNoManaged() {
        PolicyUtil.require(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL);
        PolicyUtil.require(exchange, TransactionPolicy.NO_MANAGED_TRANSACTION);
        exchange.setPhase(ExchangePhase.IN);

        try {
            handler.handleMessage(exchange);
        } catch (HandlerException handlerEx) {
            // expected
            System.out.println(handlerEx.toString());
            return;
        }

        Assert.fail("Expected a handler exception due to incompatible policy");
    }

    @Test
    public void incompatibleRequirements_managedLocalAndNoManaged() {
        PolicyUtil.require(exchange, TransactionPolicy.MANAGED_TRANSACTION_LOCAL);
        PolicyUtil.require(exchange, TransactionPolicy.NO_MANAGED_TRANSACTION);
        exchange.setPhase(ExchangePhase.IN);

        try {
            handler.handleMessage(exchange);
        } catch (HandlerException handlerEx) {
            // expected
            System.out.println(handlerEx.toString());
            return;
        }

        Assert.fail("Expected a handler exception due to incompatible policy");
    }

    @Test
    public void incompatibleRequirements_PropagatesAndManagedLocal() {
        PolicyUtil.require(exchange, TransactionPolicy.PROPAGATES_TRANSACTION);
        PolicyUtil.require(exchange, TransactionPolicy.MANAGED_TRANSACTION_LOCAL);
        exchange.setPhase(ExchangePhase.IN);

        try {
            handler.handleMessage(exchange);
        } catch (HandlerException handlerEx) {
            // expected
            System.out.println(handlerEx.toString());
            return;
        }

        Assert.fail("Expected a handler exception due to incompatible policy");
    }

    @Test
    public void incompatibleRequirements_PropagatesAndNoManaged() {
        PolicyUtil.require(exchange, TransactionPolicy.PROPAGATES_TRANSACTION);
        PolicyUtil.require(exchange, TransactionPolicy.NO_MANAGED_TRANSACTION);
        exchange.setPhase(ExchangePhase.IN);

        try {
            handler.handleMessage(exchange);
        } catch (HandlerException handlerEx) {
            // expected
            System.out.println(handlerEx.toString());
            return;
        }

        Assert.fail("Expected a handler exception due to incompatible policy");
    }

    /* invalid transaction status */
    
    @Test
    public void propagatesRequiredButNoTransaction() {
        PolicyUtil.require(exchange, TransactionPolicy.PROPAGATES_TRANSACTION);
        exchange.setPhase(ExchangePhase.IN);

        try {
            handler.handleMessage(exchange);
        } catch (HandlerException handlerEx) {
            // expected
            System.out.println(handlerEx.toString());
            return;
        }
        Assert.fail("Expected a handler exception due to invalid policy");
    }
    
    @Test
    public void propagateProvidedButNotRequired() throws Exception {
        // We currently view a propagated transaction without a requirement
        // as harmless. This tests confirms this behavior and acts as a guard
        // in case our behavior changes.
        PolicyUtil.provide(exchange, TransactionPolicy.PROPAGATES_TRANSACTION);
        exchange.setPhase(ExchangePhase.IN);
        tm.begin();
        
        try {
            handler.handleMessage(exchange);
        } catch (HandlerException handlerEx) {
            Assert.fail("Exception not expected when transaction is provided but not required: "
                    + handlerEx);
        }
    }

    /* transaction propagation */
    
    @Test
    public void propagateRequiredAndProvided() throws Exception {
        PolicyUtil.require(exchange, TransactionPolicy.PROPAGATES_TRANSACTION);
        PolicyUtil.provide(exchange, TransactionPolicy.PROPAGATES_TRANSACTION);
        exchange.setPhase(ExchangePhase.IN);

        tm.begin();
        Transaction tx = tm.getTransaction();

        handler.handleMessage(exchange);
        exchange.setPhase(ExchangePhase.OUT);
        handler.handleMessage(exchange);

        Transaction tx2 = tm.getTransaction();
        Assert.assertEquals(tx, tx2);
        Assert.assertEquals(Status.STATUS_ACTIVE, tx2.getStatus());
    }

    @Test
    public void propagateRequiredAndProvidedWithManagedGlobalRequired() throws Exception {
        PolicyUtil.require(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL);
        propagateRequiredAndProvided();
        Assert.assertTrue(PolicyUtil.isProvided(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL));
    }

    /* Creates new transaction */
    
    @Test
    public void propagateRequiredButNotProvidedWithManagedGlobalRequired() throws Exception {
        PolicyUtil.require(exchange, TransactionPolicy.PROPAGATES_TRANSACTION);
        PolicyUtil.require(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL);
        exchange.setPhase(ExchangePhase.IN);

        Assert.assertEquals(null, tm.getTransaction());

        handler.handleMessage(exchange);
        Transaction tx = tm.getTransaction();
        Assert.assertEquals(Status.STATUS_ACTIVE, tx.getStatus());
        Assert.assertTrue(PolicyUtil.isProvided(exchange, TransactionPolicy.PROPAGATES_TRANSACTION));
        Assert.assertTrue(PolicyUtil.isProvided(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL));

        exchange.setPhase(ExchangePhase.OUT);
        handler.handleMessage(exchange);
        Assert.assertEquals(Status.STATUS_COMMITTED, tx.getStatus());
    }

    @Test
    public void managedLocalRequiredWithNoTransactionProvided() throws Exception {
        PolicyUtil.require(exchange, TransactionPolicy.MANAGED_TRANSACTION_LOCAL);
        exchange.setPhase(ExchangePhase.IN);
        
        Assert.assertEquals(null, tm.getTransaction());
        
        handler.handleMessage(exchange);
        Transaction tx = tm.getTransaction();
        Assert.assertEquals(Status.STATUS_ACTIVE, tx.getStatus());
        Assert.assertTrue(PolicyUtil.isProvided(exchange, TransactionPolicy.MANAGED_TRANSACTION_LOCAL));
        
        exchange.setPhase(ExchangePhase.OUT);
        handler.handleMessage(exchange);
        Assert.assertEquals(Status.STATUS_COMMITTED, tx.getStatus());
    }
    
    @Test
    public void transactionRolledbackByHandler() throws Exception {
        PolicyUtil.require(exchange, TransactionPolicy.PROPAGATES_TRANSACTION);
        PolicyUtil.require(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL);
        exchange.setPhase(ExchangePhase.IN);

        Assert.assertEquals(null, tm.getTransaction());

        handler.handleMessage(exchange);
        Transaction tx = tm.getTransaction();
        Assert.assertEquals(Status.STATUS_ACTIVE, tx.getStatus());
        Assert.assertTrue(PolicyUtil.isProvided(exchange, TransactionPolicy.PROPAGATES_TRANSACTION));
        Assert.assertTrue(PolicyUtil.isProvided(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL));

        tx.setRollbackOnly();
        exchange.setPhase(ExchangePhase.OUT);
        handler.handleMessage(exchange);
        Assert.assertEquals(Status.STATUS_ROLLEDBACK, tx.getStatus());
    }

    @Test
    public void suspendAndManagedGlobalRequiredButNoTransaction() throws Exception {
        PolicyUtil.require(exchange, TransactionPolicy.SUSPENDS_TRANSACTION);
        PolicyUtil.require(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL);
        exchange.setPhase(ExchangePhase.IN);

        handler.handleMessage(exchange);
        Transaction tx2 = tm.getTransaction();
        Assert.assertEquals(Status.STATUS_ACTIVE, tx2.getStatus());
        Assert.assertTrue(PolicyUtil.isProvided(exchange, TransactionPolicy.SUSPENDS_TRANSACTION));
        Assert.assertTrue(PolicyUtil.isProvided(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL));
        
        exchange.setPhase(ExchangePhase.OUT);
        handler.handleMessage(exchange);
        Assert.assertEquals(Status.STATUS_COMMITTED, tx2.getStatus());
    }

    /* Suspends existing transaction and create new transaction */
    
    @Test
    public void suspendAndManagedGlobalRequired() throws Exception {
        PolicyUtil.require(exchange, TransactionPolicy.SUSPENDS_TRANSACTION);
        PolicyUtil.require(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL);
        exchange.setPhase(ExchangePhase.IN);
        tm.begin();
        Transaction tx1 = tm.getTransaction();

        handler.handleMessage(exchange);
        Transaction tx2 = tm.getTransaction();
        Assert.assertNotSame(tx1, tx2);
        Assert.assertEquals(Status.STATUS_ACTIVE, tx1.getStatus());
        Assert.assertEquals(Status.STATUS_ACTIVE, tx2.getStatus());
        Assert.assertTrue(PolicyUtil.isProvided(exchange, TransactionPolicy.SUSPENDS_TRANSACTION));
        Assert.assertTrue(PolicyUtil.isProvided(exchange, TransactionPolicy.MANAGED_TRANSACTION_GLOBAL));
        
        exchange.setPhase(ExchangePhase.OUT);
        handler.handleMessage(exchange);
        Assert.assertEquals(tx1, tm.getTransaction());
        Assert.assertEquals(Status.STATUS_ACTIVE, tx1.getStatus());
        Assert.assertEquals(Status.STATUS_COMMITTED, tx2.getStatus());
    }

    @Test
    public void suspendsAndManagedLocalRequired() throws Exception {
        PolicyUtil.require(exchange, TransactionPolicy.SUSPENDS_TRANSACTION);
        PolicyUtil.require(exchange, TransactionPolicy.MANAGED_TRANSACTION_LOCAL);
        exchange.setPhase(ExchangePhase.IN);
        tm.begin();
        Transaction tx1 = tm.getTransaction();
        
        handler.handleMessage(exchange);
        Transaction tx2 = tm.getTransaction();
        Assert.assertNotSame(tx1, tx2);
        Assert.assertEquals(Status.STATUS_ACTIVE, tx1.getStatus());
        Assert.assertEquals(Status.STATUS_ACTIVE, tx2.getStatus());
        Assert.assertTrue(PolicyUtil.isProvided(exchange, TransactionPolicy.SUSPENDS_TRANSACTION));
        Assert.assertTrue(PolicyUtil.isProvided(exchange, TransactionPolicy.MANAGED_TRANSACTION_LOCAL));
        
        exchange.setPhase(ExchangePhase.OUT);
        handler.handleMessage(exchange);
        Transaction tx3 = tm.getTransaction();
        Assert.assertEquals(tx1, tx3);
        Assert.assertEquals(Status.STATUS_COMMITTED, tx2.getStatus());
        Assert.assertEquals(Status.STATUS_ACTIVE, tx3.getStatus());
    }

    /* Suspends existing transaction and run under no managed transaction */
    
    @Test
    public void noManagedRequired() throws Exception {
        PolicyUtil.require(exchange, TransactionPolicy.NO_MANAGED_TRANSACTION);
        exchange.setPhase(ExchangePhase.IN);
        tm.begin();
        Transaction tx1 = tm.getTransaction();
        
        handler.handleMessage(exchange);
        Transaction tx2 = tm.getTransaction();
        Assert.assertNotNull(tx1);
        Assert.assertNull(tx2);
        Assert.assertEquals(Status.STATUS_ACTIVE, tx1.getStatus());
        Assert.assertTrue(PolicyUtil.isProvided(exchange, TransactionPolicy.NO_MANAGED_TRANSACTION));
        
        exchange.setPhase(ExchangePhase.OUT);
        handler.handleMessage(exchange);
        Transaction tx3 = tm.getTransaction();
        Assert.assertEquals(tx1, tx3);
        Assert.assertEquals(Status.STATUS_ACTIVE, tx3.getStatus());
    }

    @Test
    public void suspendsRequired() throws Exception {
        PolicyUtil.require(exchange, TransactionPolicy.SUSPENDS_TRANSACTION);
        exchange.setPhase(ExchangePhase.IN);
        tm.begin();
        Transaction tx1 = tm.getTransaction();

        handler.handleMessage(exchange);
        // transaction should be disabled
        Assert.assertNull(tm.getTransaction());
        Assert.assertTrue(PolicyUtil.isProvided(exchange, TransactionPolicy.SUSPENDS_TRANSACTION));

        exchange.setPhase(ExchangePhase.OUT);
        handler.handleMessage(exchange);
        Transaction tx2 = tm.getTransaction();
        // transaction should be enabled
        Assert.assertEquals(tx1, tx2);
        Assert.assertEquals(Status.STATUS_ACTIVE, tx2.getStatus());
    }
    
    @Test
    public void suspendAndNoManagedRequired() throws Exception {
        PolicyUtil.require(exchange, TransactionPolicy.NO_MANAGED_TRANSACTION);
        suspendsRequired();
        Assert.assertTrue(PolicyUtil.isProvided(exchange, TransactionPolicy.NO_MANAGED_TRANSACTION));
    }
    
    /* no transaction */
    
    @Test
    public void suspendRequiredButNoTransaction() throws Exception {
        PolicyUtil.require(exchange, TransactionPolicy.SUSPENDS_TRANSACTION);
        exchange.setPhase(ExchangePhase.IN);
        handler.handleMessage(exchange);
        Assert.assertNull(tm.getTransaction());
        exchange.setPhase(ExchangePhase.OUT);
        handler.handleMessage(exchange);
        Assert.assertNull(tm.getTransaction());
        Assert.assertTrue(PolicyUtil.isProvided(exchange, TransactionPolicy.SUSPENDS_TRANSACTION));
    }
    
    @Test
    public void suspendAndNoManagedRequiredButNoTransaction() throws Exception {
        PolicyUtil.require(exchange, TransactionPolicy.NO_MANAGED_TRANSACTION);
        suspendRequiredButNoTransaction();
        Assert.assertTrue(PolicyUtil.isProvided(exchange, TransactionPolicy.NO_MANAGED_TRANSACTION));
    }
}

class MockTransactionManager implements TransactionManager {

    private ThreadLocal<MockTransaction> transaction = new ThreadLocal<MockTransaction>();

    @Override
    public void begin() throws NotSupportedException, SystemException {
        transaction.set(new MockTransaction());
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException,
            IllegalStateException, SystemException {
        transaction.get().commit();
        transaction.remove();
    }

    @Override
    public int getStatus() throws SystemException {
        if (transaction.get() != null) {
            return transaction.get().getStatus();
        } else {
            return Status.STATUS_NO_TRANSACTION;
        }
    }

    @Override
    public MockTransaction getTransaction() throws SystemException {
        return transaction.get();
    }

    @Override
    public void resume(Transaction arg0) throws InvalidTransactionException,
            IllegalStateException, SystemException {
        transaction.set(MockTransaction.class.cast(arg0));
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException,
            SystemException {
        transaction.get().rollback();
        transaction.remove();
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        transaction.get().setRollbackOnly();
    }

    @Override
    public void setTransactionTimeout(int arg0) throws SystemException {
        // TODO Auto-generated method stub

    }

    @Override
    public Transaction suspend() throws SystemException {
        MockTransaction t = transaction.get();
        transaction.remove();
        return t;
    }
}

class MockTransaction implements Transaction {

    private int status = Status.STATUS_ACTIVE;
    private String uuid;
    
    public MockTransaction() {
        uuid = UUID.randomUUID().toString();
    }
    
    @Override
    public void commit() throws RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException,
            IllegalStateException, SystemException {
        status = Status.STATUS_COMMITTED;
    }

    @Override
    public boolean delistResource(XAResource arg0, int arg1)
            throws IllegalStateException, SystemException {
        return false;
    }

    @Override
    public boolean enlistResource(XAResource arg0) throws RollbackException,
            IllegalStateException, SystemException {
        return false;
    }

    @Override
    public int getStatus() throws SystemException {
        return status;
    }

    @Override
    public void registerSynchronization(Synchronization arg0)
            throws RollbackException, IllegalStateException, SystemException {
    }

    @Override
    public void rollback() throws IllegalStateException, SystemException {
        status = Status.STATUS_ROLLEDBACK;
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        status = Status.STATUS_MARKED_ROLLBACK;
    }

    public String getUUID() {
        return uuid;
    }
    
    @Override
    public boolean equals(Object obj) {
        MockTransaction target = MockTransaction.class.cast(obj);
        return uuid.equals(target.getUUID());
    }
}