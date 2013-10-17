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
package org.switchyard.config.model.switchyard;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.SCABindingModel;
import org.switchyard.config.model.composite.v1.V1SCABindingModel;

/**
 * SCABindingExtensionTests.
 */
public class SCABindingExtensionTests {

    private static final String SCA_BINDING_XML = "/org/switchyard/config/model/switchyard/SCABindingTests.xml";

    private ModelPuller<SwitchYardModel> _puller;

    @Before
    public void before() throws Exception {
        _puller = new ModelPuller<SwitchYardModel>();
    }
    
    @Test
    public void testCreate() throws Exception {
        final String TARGET = "foo";
        final String TARGET_NS = "urn:bar";
        final String STRATEGY = "RoundRobin";
        
        SCABindingModel scab = new V1SCABindingModel(SwitchYardNamespace.DEFAULT.uri());
        scab.setClustered(true)
            .setLoadBalance(STRATEGY)
            .setTarget(TARGET)
            .setTargetNamespace(TARGET_NS);
        
       Assert.assertEquals(STRATEGY, scab.getLoadBalance());
       Assert.assertEquals(TARGET, scab.getTarget());
       Assert.assertEquals(TARGET_NS, scab.getTargetNamespace());

       Assert.assertTrue(scab.isClustered());
       Assert.assertTrue(scab.hasTarget());
       Assert.assertTrue(scab.hasTargetNamespace());
       Assert.assertTrue(scab.isLoadBalanced());
    }

    @Test
    public void testRead() throws Exception {
        SwitchYardModel switchyard = _puller.pull(SCA_BINDING_XML, getClass());
        SCABindingModel sb = (SCABindingModel)switchyard.getComposite().getServices().get(0).getBindings().get(0);
        SCABindingModel rb = (SCABindingModel)switchyard.getComposite().getReferences().get(0).getBindings().get(0);
        
        Assert.assertTrue(sb.isClustered());
        Assert.assertFalse(sb.isLoadBalanced());
        Assert.assertFalse(sb.hasTarget());
        Assert.assertFalse(sb.hasTargetNamespace());
        
        Assert.assertTrue(rb.isClustered());
        Assert.assertEquals("RoundRobin", rb.getLoadBalance());
        Assert.assertEquals("somethingElse", rb.getTarget());
        Assert.assertEquals("urn:another:uri", rb.getTargetNamespace());
    }

    @Test
    public void testValidation() throws Exception {
        SwitchYardModel switchyard = _puller.pull(SCA_BINDING_XML, getClass());
        switchyard.assertModelValid();
    }

}
