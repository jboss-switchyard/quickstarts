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

import java.io.StringReader;

import javax.xml.namespace.QName;

import org.junit.Assert;
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

    private static final QName POLICY_FOO = new QName("foo");
    private static final QName POLICY_BAR = new QName("bar");

    private static final String CUSTOM_NS = "http://www.custom.org/ns/custom/1.0";
    private static final QName POLICY_THINGAMAGIG = new QName(CUSTOM_NS, "thingamagig", "cstm");
    private static final QName POLICY_NONAMESPACE_CLIENT_AUTHENTICATION = new QName("clientAuthentication");
    private static final QName POLICY_QUALIFIED_AUTHORIZATION = new QName("http://docs.oasis-open.org/ns/opencsa/sca/200912", "authorization", "sca");

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
        Assert.assertTrue(svc.hasPolicyRequirement(POLICY_FOO));
        Assert.assertTrue(svc.hasPolicyRequirement(POLICY_BAR));
    }

    @Test
    public void requiresOnReference() throws Exception {
        ComponentModel comp = _model.getComponents().get(0);
        ComponentReferenceModel ref = comp.getReferences().get(0);
        Assert.assertTrue(ref.hasPolicyRequirement(POLICY_BAR));
    }

    @Test
    public void buildPolicyConfig() throws Exception {
        // Test service policy
        ComponentServiceModel svc = new V1ComponentServiceModel(SwitchYardNamespace.DEFAULT.uri());
        svc.addPolicyRequirement(POLICY_FOO);
        svc.addPolicyRequirement(POLICY_BAR);
        Assert.assertTrue(svc.hasPolicyRequirement(POLICY_FOO));
        Assert.assertTrue(svc.hasPolicyRequirement(POLICY_BAR));

        // Test reference policy
        ComponentReferenceModel ref = new V1ComponentReferenceModel(SwitchYardNamespace.DEFAULT.uri());
        ref.addPolicyRequirement(POLICY_BAR);
        Assert.assertTrue(ref.hasPolicyRequirement(POLICY_BAR));
        Assert.assertFalse(ref.hasPolicyRequirement(POLICY_FOO));
        svc.addPolicyRequirement(POLICY_FOO);
        Assert.assertTrue(svc.hasPolicyRequirement(POLICY_FOO));
    }

    @Test
    public void testAddedRequirements() throws Exception {
        _model.getModelConfiguration().setAttribute("xmlns:cstm", CUSTOM_NS);
        // a re-pull is necessary because of how we're setting the attribute above
        _model =  new ModelPuller<CompositeModel>().pull(new StringReader(_model.toString()));
        ComponentModel comp = _model.getComponents().get(0);
        ComponentServiceModel svc = comp.getServices().get(0);
        svc.addPolicyRequirement(POLICY_THINGAMAGIG);
        svc.addPolicyRequirement(POLICY_NONAMESPACE_CLIENT_AUTHENTICATION);
        svc.addPolicyRequirement(POLICY_QUALIFIED_AUTHORIZATION);
        Assert.assertTrue(svc.hasPolicyRequirement(POLICY_FOO));
        Assert.assertTrue(svc.hasPolicyRequirement(POLICY_BAR));
        Assert.assertTrue(svc.hasPolicyRequirement(POLICY_THINGAMAGIG));
        Assert.assertTrue(svc.hasPolicyRequirement(POLICY_NONAMESPACE_CLIENT_AUTHENTICATION));
        Assert.assertTrue(svc.hasPolicyRequirement(POLICY_QUALIFIED_AUTHORIZATION));
    }
}
