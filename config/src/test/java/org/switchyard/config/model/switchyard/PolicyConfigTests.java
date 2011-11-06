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

import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.v1.V1ComponentReferenceModel;
import org.switchyard.config.model.composite.v1.V1ComponentServiceModel;

/**
 * Test policy-related configuration in composite model.
 */
public class PolicyConfigTests {

	private static final String POLICY_FOO = "foo";
	private static final String POLICY_BAR = "bar";
	
    private static final String POLICY_XML = 
    		"/org/switchyard/config/model/composite/PolicyTests.xml";
    
    private CompositeModel _model;

    @Before
    public void before() throws Exception {
    	ModelPuller<CompositeModel> puller = new ModelPuller<CompositeModel>();
    	_model = puller.pull(POLICY_XML, getClass());
    }

	@Test
	public void requiresOnService() throws Exception {
		ComponentModel comp = _model.getComponents().get(0);
		ComponentServiceModel svc = comp.getServices().get(0);
		Set<String> requires = svc.getPolicyRequirements();
		Assert.assertEquals(2, requires.size());
		Assert.assertTrue(requires.contains(POLICY_FOO));
		Assert.assertTrue(requires.contains(POLICY_BAR));
	}
	
	@Test
	public void requiresOnReference() throws Exception {
		ComponentModel comp = _model.getComponents().get(0);
		ComponentReferenceModel ref = comp.getReferences().get(0);
		Set<String> requires = ref.getPolicyRequirements();
		Assert.assertEquals(1, requires.size());
		Assert.assertTrue(requires.contains(POLICY_BAR));
	}
	
	@Test
	public void buildPolicyConfig() throws Exception {
		// Test service policy
		ComponentServiceModel svc = new V1ComponentServiceModel();
		svc.addPolicyRequirement(POLICY_FOO);
		svc.addPolicyRequirement(POLICY_BAR);
		Set<String> svcRequires = svc.getPolicyRequirements();
		Assert.assertTrue(svcRequires.contains(POLICY_FOO));
		Assert.assertTrue(svcRequires.contains(POLICY_BAR));
		
		// Test reference policy
		ComponentReferenceModel ref = new V1ComponentReferenceModel();
		ref.addPolicyRequirement(POLICY_BAR);
		Set<String> refRequires = ref.getPolicyRequirements();
		Assert.assertTrue(refRequires.contains(POLICY_BAR));
		Assert.assertFalse(refRequires.contains(POLICY_FOO));
		svc.addPolicyRequirement(POLICY_FOO);
		Assert.assertTrue(svcRequires.contains(POLICY_FOO));
		
		
	}
}
