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

package org.switchyard.metadata;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.ServiceMetadata;
import org.switchyard.metadata.qos.Throttling;
import org.switchyard.policy.Policy;
import org.switchyard.policy.TransactionPolicy;

public class ServiceMetadataBuilderTest {
    
    @Test
    public void buildMetadata() {
        // build metadata pieces
        Registrant registrant = new MockRegistrant();
        List<Policy> required = new LinkedList<Policy>();
        required.add(TransactionPolicy.SUSPENDS_TRANSACTION);
        List<Policy> provided = new LinkedList<Policy>();
        provided.add(TransactionPolicy.PROPAGATES_TRANSACTION);
        Throttling throttling = new Throttling();
        
        // add them to service metadata
        ServiceMetadata metadata = ServiceMetadataBuilder.create()
            .providedPolicies(provided)
            .requiredPolicies(required)
            .registrant(registrant)
            .throttling(throttling)
            .build();
        
        // verify
        Assert.assertEquals(registrant, metadata.getRegistrant());
        Assert.assertEquals(throttling, metadata.getThrottling());
        Assert.assertEquals(required.get(0), metadata.getRequiredPolicies().get(0));
        Assert.assertEquals(provided.get(0), metadata.getProvidedPolicies().get(0));
    }
    
    @Test
    public void testUpdate() {
        Throttling throttling1 = new Throttling().setMaxRequests(50);
        ServiceMetadata metadata = ServiceMetadataBuilder.create()
                .throttling(throttling1)
                .build();
        
        Assert.assertEquals(50, metadata.getThrottling().getMaxRequests());
        
        Throttling throttling2 = new Throttling().setMaxRequests(150);
        ServiceMetadataBuilder.update(metadata).throttling(throttling2);
        Assert.assertEquals(150, metadata.getThrottling().getMaxRequests());
    }
}


class MockRegistrant implements Registrant {

    @Override
    public boolean isBinding() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isImplementation() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <T> T getConfig() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
