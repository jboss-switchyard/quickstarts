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

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
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
import org.switchyard.internal.DefaultContext;
import org.switchyard.policy.ExchangePolicy;
import org.switchyard.policy.TransactionPolicy;


public class TransactionHandlerTest {
	
	private MockTransactionManager tm;
	private MockExchange exchange;
	private TransactionHandler handler;
	
	@Before
	public void setUp() {
		tm = new MockTransactionManager();
		exchange = new MockExchange();
		exchange.setContext(new DefaultContext());
		handler = new TransactionHandler();
		handler.setTransactionManager(tm);
	}
	
	@Test
	public void incompatibleRequirements() {
		ExchangePolicy.require(exchange, TransactionPolicy.PROPAGATE);
		ExchangePolicy.require(exchange, TransactionPolicy.SUSPEND);
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
	public void propagateProvidedButNotRequired() {
		// We currently view a propagated transaction without a requirement
		// as harmless.  This tests confirms this behavior and acts as a guard
		// in case our behavior changes.
		ExchangePolicy.provide(exchange, TransactionPolicy.PROPAGATE);
		exchange.setPhase(ExchangePhase.IN);

		try {
			handler.handleMessage(exchange);
		} catch (HandlerException handlerEx) {
			Assert.fail("Exception not expected when transaction is provided but not required: " + handlerEx);
		}
	}
	
	@Test
	public void propagateRequiredButNotProvided() {
		ExchangePolicy.require(exchange, TransactionPolicy.PROPAGATE);
		exchange.setPhase(ExchangePhase.IN);
		
		try {
			handler.handleMessage(exchange);
		} catch (HandlerException handlerEx) {
			// expected
			System.out.println(handlerEx.toString());
			return;
		}
		
		Assert.fail("Handler should fail when propagation required, but not provided");
	}
	
	@Test
	public void suspendTransaction() throws Exception {
		ExchangePolicy.require(exchange, TransactionPolicy.SUSPEND);
		exchange.setPhase(ExchangePhase.IN);
		handler.handleMessage(exchange);
		// transaction should be disabled
		Assert.assertFalse(tm.getTransaction().active);
		exchange.setPhase(ExchangePhase.OUT);
		handler.handleMessage(exchange);
		// transaction should be enabled
		Assert.assertTrue(tm.getTransaction().active);
	}
}

class MockTransactionManager implements TransactionManager {
	
	private ThreadLocal<MockTransaction> transaction = new ThreadLocal<MockTransaction>();
	
	MockTransactionManager() {
		transaction.set(new MockTransaction());
	}

	@Override
	public void begin() throws NotSupportedException, SystemException {		
	}

	@Override
	public void commit() throws RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SecurityException,
			IllegalStateException, SystemException {
	}
	
	@Override
	public int getStatus() throws SystemException {
		return 0;
	}

	@Override
	public MockTransaction getTransaction() throws SystemException {
		return transaction.get();
	}

	@Override
	public void resume(Transaction arg0) throws InvalidTransactionException,
			IllegalStateException, SystemException {
		transaction.get().active = true;
	}

	@Override
	public void rollback() throws IllegalStateException, SecurityException,
			SystemException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTransactionTimeout(int arg0) throws SystemException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Transaction suspend() throws SystemException {
		MockTransaction t = transaction.get();
		t.active = false;
		return t;
	}
}

class MockTransaction implements Transaction {
	
	boolean active = true;
	
	@Override
	public void commit() throws RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SecurityException,
			IllegalStateException, SystemException {		
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
		return 0;
	}

	@Override
	public void registerSynchronization(Synchronization arg0)
			throws RollbackException, IllegalStateException, SystemException {		
	}

	@Override
	public void rollback() throws IllegalStateException, SystemException {
		
	}

	@Override
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		
	}
	
}