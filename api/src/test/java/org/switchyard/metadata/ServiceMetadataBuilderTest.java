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
