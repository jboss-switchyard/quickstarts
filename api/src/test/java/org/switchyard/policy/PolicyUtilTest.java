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
