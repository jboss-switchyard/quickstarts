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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.MockExchange;
import org.switchyard.policy.Policy;
import org.switchyard.policy.PolicyUtil;

public class PolicyHandlerTest {
    
    static final String FOO_POLICY_NAME = "foo";
    static final String BAR_POLICY_NAME = "bar";
	
	private MockExchange exchange;
	private PolicyHandler handler;
	
	@Before
	public void setUp() {
		exchange = new MockExchange();
		handler = new PolicyHandler();
	}
	
	@Test
	public void requiredButNotProvided() {
		PolicyUtil.require(exchange, new FooPolicy());
		PolicyUtil.require(exchange, new BarPolicy());
        PolicyUtil.provide(exchange, new BarPolicy());
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
    public void providedButNotRequired() {
	    // this should not be an error
	    PolicyUtil.provide(exchange, new FooPolicy());
        exchange.setPhase(ExchangePhase.IN);

        try {
            handler.handleMessage(exchange);
        } catch (HandlerException handlerEx) {
            Assert.fail("Exception not expected when policy is provided but not required: " + handlerEx);
        }
    }
	
	@Test
    public void requiredAndProvided() {
        PolicyUtil.require(exchange, new FooPolicy());
        PolicyUtil.provide(exchange, new FooPolicy());
        exchange.setPhase(ExchangePhase.IN);

        try {
            handler.handleMessage(exchange);
        } catch (HandlerException handlerEx) {
            Assert.fail("Exception not expected, required policy is provided: " + handlerEx);
        }
    }
	
	class FooPolicy implements Policy {
	    @Override
	    public String getName() {
	        return FOO_POLICY_NAME;
	    }

        @Override
        public PolicyType getType() {
            return PolicyType.INTERACTION;
        }

        @Override
        public boolean isCompatibleWith(Policy target) {
            return true;
        }
        
        @Override
        public Policy getPolicyDependency() {
            return null;
        }
	}
	
	class BarPolicy implements Policy {
        @Override
        public String getName() {
            return BAR_POLICY_NAME;
        }

        @Override
        public PolicyType getType() {
            return PolicyType.INTERACTION;
        }

        @Override
        public boolean isCompatibleWith(Policy target) {
            return true;
        }
        
        @Override
        public Policy getPolicyDependency() {
            return null;
        }
    }
}

