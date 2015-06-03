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

package org.switchyard.component.bean.tests;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.policy.SecurityPolicy;
import org.switchyard.policy.TransactionPolicy;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class RequiredPoliciesTest {
    
    private SwitchYardModel _scannedModel;
    
    @Before
    public void setUp() throws Exception {
        BeanSwitchYardScanner scanner = new BeanSwitchYardScanner();
        List<URL> urls = new ArrayList<URL>();

        // If running this test inside your IDE... you need to set the cwd to be the
        // root of the bean module !!
        urls.add(new File("./target/test-classes").toURI().toURL());

        ScannerInput<SwitchYardModel> input = new ScannerInput<SwitchYardModel>()
                                                    .setURLs(urls)
                                                    .setExcludePackages(BeanUTConstants.BEAN_SCANNER_BLACK_LIST);
        _scannedModel = scanner.scan(input).getModel();
    }

    @Test
    public void verifyPolicyGenerated() throws Exception {
        List<ComponentModel> components = _scannedModel.getComposite().getComponents();
        
        boolean ssFound = false;
        boolean ltsFound = false;
        boolean stsFound = false;
        
        for(ComponentModel component : components) {
            if(component.getName().equals("SecureService")) {
                ComponentImplementationModel impl = component.getImplementation();
                Assert.assertTrue(impl.hasPolicyRequirement(SecurityPolicy.AUTHORIZATION.getQName()));
                ComponentServiceModel svc = component.getServices().get(0);
                Assert.assertTrue(svc.hasPolicyRequirement(SecurityPolicy.CLIENT_AUTHENTICATION.getQName()));
                Assert.assertTrue(svc.hasPolicyRequirement(SecurityPolicy.CONFIDENTIALITY.getQName()));
                ComponentReferenceModel ref = component.getReferences().get(0);
                Assert.assertTrue(ref.hasPolicyRequirement(SecurityPolicy.AUTHORIZATION.getQName()));
                Assert.assertTrue(ref.hasPolicyRequirement(SecurityPolicy.CLIENT_AUTHENTICATION.getQName()));
                Assert.assertTrue(ref.hasPolicyRequirement(SecurityPolicy.CONFIDENTIALITY.getQName()));
                ssFound = true;
                continue;
            }
            
            if(component.getName().equals("LocalTransactionService")) {
                ComponentServiceModel svc = component.getServices().get(0);
                Assert.assertTrue(svc.hasPolicyRequirement(TransactionPolicy.SUSPENDS_TRANSACTION.getQName()));
                ComponentReferenceModel ref = component.getReferences().get(0);
                Assert.assertTrue(ref.hasPolicyRequirement(TransactionPolicy.SUSPENDS_TRANSACTION.getQName()));
                ltsFound = true;
                continue;
            }
            
            if(component.getName().equals("SharedTransactionService")) {
                ComponentServiceModel svc = component.getServices().get(0);
                Assert.assertTrue(svc.hasPolicyRequirement(TransactionPolicy.PROPAGATES_TRANSACTION.getQName()));
                ComponentReferenceModel ref = component.getReferences().get(0);
                Assert.assertTrue(ref.hasPolicyRequirement(TransactionPolicy.PROPAGATES_TRANSACTION.getQName()));
                stsFound = true;
                continue;
            }
        }
        
        Assert.assertTrue("SecureService not discovered!", ssFound);
        Assert.assertTrue("LocalTransactionService not discovered!", ltsFound);
        Assert.assertTrue("SharedTransactionService not discovered!", stsFound);
    }

}
