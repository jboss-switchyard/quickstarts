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
import org.switchyard.policy.Policy.PolicyType;

public class PolicyFactoryTest {

    @Test
    public void testFactory() throws Exception {
        Policy p = PolicyFactory.getPolicy("managedTransaction.Global");
        Assert.assertEquals(TransactionPolicy.MANAGED_TRANSACTION_GLOBAL, p);
        Assert.assertEquals(PolicyType.IMPLEMENTATION, p.getType());
        p = PolicyFactory.getPolicy("managedTransaction.Local");
        Assert.assertEquals(TransactionPolicy.MANAGED_TRANSACTION_LOCAL, p);
        Assert.assertEquals(PolicyType.IMPLEMENTATION, p.getType());
        p = PolicyFactory.getPolicy("noManagedTransaction");
        Assert.assertEquals(TransactionPolicy.NO_MANAGED_TRANSACTION, p);
        Assert.assertEquals(PolicyType.IMPLEMENTATION, p.getType());
        p = PolicyFactory.getPolicy("propagatesTransaction");
        Assert.assertEquals(TransactionPolicy.PROPAGATES_TRANSACTION, p);
        Assert.assertEquals(PolicyType.INTERACTION, p.getType());
        p = PolicyFactory.getPolicy("suspendsTransaction");
        Assert.assertEquals(TransactionPolicy.SUSPENDS_TRANSACTION, p);
        Assert.assertEquals(PolicyType.INTERACTION, p.getType());

        p = PolicyFactory.getPolicy("clientAuthentication");
        Assert.assertEquals(SecurityPolicy.CLIENT_AUTHENTICATION, p);
        Assert.assertEquals(PolicyType.INTERACTION, p.getType());
        p = PolicyFactory.getPolicy("confidentiality");
        Assert.assertEquals(SecurityPolicy.CONFIDENTIALITY, p);
        Assert.assertEquals(PolicyType.INTERACTION, p.getType());
    }
    
}
