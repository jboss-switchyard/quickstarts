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
        
        SCABindingModel scab = new V1SCABindingModel();
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
