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
import org.switchyard.policy.Policy.PolicyType;

public class PolicyFactoryTest {

    @Test
    @SuppressWarnings("deprecation")
    public void testFactory() throws Exception {
        // TransactionPolicy
        Policy p = PolicyFactory.getPolicy("managedTransaction.Global");
        Assert.assertEquals(TransactionPolicy.MANAGED_TRANSACTION_GLOBAL, p);
        Assert.assertTrue(p.supports(PolicyType.IMPLEMENTATION));
        p = PolicyFactory.getPolicy("managedTransaction.Local");
        Assert.assertEquals(TransactionPolicy.MANAGED_TRANSACTION_LOCAL, p);
        Assert.assertTrue(p.supports(PolicyType.IMPLEMENTATION));
        p = PolicyFactory.getPolicy("noManagedTransaction");
        Assert.assertEquals(TransactionPolicy.NO_MANAGED_TRANSACTION, p);
        Assert.assertTrue(p.supports(PolicyType.IMPLEMENTATION));
        p = PolicyFactory.getPolicy("propagatesTransaction");
        Assert.assertEquals(TransactionPolicy.PROPAGATES_TRANSACTION, p);
        Assert.assertTrue(p.supports(PolicyType.INTERACTION));
        p = PolicyFactory.getPolicy("suspendsTransaction");
        Assert.assertEquals(TransactionPolicy.SUSPENDS_TRANSACTION, p);
        Assert.assertTrue(p.supports(PolicyType.INTERACTION));
        // SecurityPolicy
        p = PolicyFactory.getPolicy("authorization");
        Assert.assertEquals(SecurityPolicy.AUTHORIZATION, p);
        Assert.assertTrue(p.supports(PolicyType.IMPLEMENTATION));
        Assert.assertTrue(p.supports(PolicyType.INTERACTION));
        p = PolicyFactory.getPolicy("clientAuthentication");
        Assert.assertEquals(SecurityPolicy.CLIENT_AUTHENTICATION, p);
        Assert.assertTrue(p.supports(PolicyType.INTERACTION));
        p = PolicyFactory.getPolicy("confidentiality");
        Assert.assertEquals(SecurityPolicy.CONFIDENTIALITY, p);
        Assert.assertTrue(p.supports(PolicyType.INTERACTION));
        // Generic Policy
        p = PolicyFactory.getPolicy("myGenericPolicy");
        Assert.assertEquals("myGenericPolicy", p.getName());
        Assert.assertTrue(p.supports(PolicyType.IMPLEMENTATION));
        Assert.assertTrue(p.supports(PolicyType.INTERACTION));
    }

}
