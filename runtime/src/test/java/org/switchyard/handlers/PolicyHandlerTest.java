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
        public boolean supports(PolicyType type) {
            return type == PolicyType.INTERACTION;
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
        public boolean supports(PolicyType type) {
            return type == PolicyType.INTERACTION;
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

