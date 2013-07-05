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
