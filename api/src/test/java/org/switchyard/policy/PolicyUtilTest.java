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

package org.switchyard.policy;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.Exchange;
import org.switchyard.MockContext;
import org.switchyard.MockExchange;

public class PolicyUtilTest {

    @Test
    public void testProvidePolicy() {
        Exchange ex = new MockExchange().setContext(new MockContext());
        PolicyUtil.provide(ex, DummyPolicy.A);
        Assert.assertTrue(PolicyUtil.isProvided(ex, DummyPolicy.A));
        Assert.assertFalse(PolicyUtil.isProvided(ex, DummyPolicy.B));
        Assert.assertFalse(PolicyUtil.isRequired(ex, DummyPolicy.B));
        Assert.assertEquals(1, PolicyUtil.getProvided(ex).size());
    }
    
    @Test
    public void testRequirePolicy() {
        Exchange ex = new MockExchange().setContext(new MockContext());
        PolicyUtil.require(ex, DummyPolicy.A);
        Assert.assertTrue(PolicyUtil.isRequired(ex, DummyPolicy.A));
        Assert.assertFalse(PolicyUtil.isRequired(ex, DummyPolicy.B));
        Assert.assertFalse(PolicyUtil.isProvided(ex, DummyPolicy.A));
        Assert.assertEquals(1, PolicyUtil.getRequired(ex).size());
    }
}

enum DummyPolicy implements Policy {
    
    A, B;
    
    @Override
    public String getName() {
        return toString();
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
