/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

package org.switchyard.component.bean.tests;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.config.model.ScannerInput;
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

        ScannerInput<SwitchYardModel> input = new ScannerInput<SwitchYardModel>().setURLs(urls);
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
                ComponentServiceModel svc = component.getServices().get(0);
                Assert.assertTrue(svc.hasPolicyRequirement(SecurityPolicy.CLIENT_AUTHENTICATION.getName()));
                Assert.assertTrue(svc.hasPolicyRequirement(SecurityPolicy.CONFIDENTIALITY.getName()));
                ComponentReferenceModel ref = component.getReferences().get(0);
                Assert.assertTrue(ref.hasPolicyRequirement(SecurityPolicy.CLIENT_AUTHENTICATION.getName()));
                Assert.assertTrue(ref.hasPolicyRequirement(SecurityPolicy.CONFIDENTIALITY.getName()));
                ssFound = true;
                continue;
            }
            
            if(component.getName().equals("LocalTransactionService")) {
            	ComponentServiceModel svc = component.getServices().get(0);
            	Assert.assertTrue(svc.hasPolicyRequirement(TransactionPolicy.SUSPENDS_TRANSACTION.getName()));
            	ComponentReferenceModel ref = component.getReferences().get(0);
            	Assert.assertTrue(ref.hasPolicyRequirement(TransactionPolicy.SUSPENDS_TRANSACTION.getName()));
            	ltsFound = true;
            	continue;
            }
            
            if(component.getName().equals("SharedTransactionService")) {
            	ComponentServiceModel svc = component.getServices().get(0);
            	Assert.assertTrue(svc.hasPolicyRequirement(TransactionPolicy.PROPAGATES_TRANSACTION.getName()));
            	ComponentReferenceModel ref = component.getReferences().get(0);
            	Assert.assertTrue(ref.hasPolicyRequirement(TransactionPolicy.PROPAGATES_TRANSACTION.getName()));
            	stsFound = true;
            	continue;
            }
        }
        
        Assert.assertTrue("SecureService not discovered!", ssFound);
        Assert.assertTrue("LocalTransactionService not discovered!", ltsFound);
        Assert.assertTrue("SharedTransactionService not discovered!", stsFound);
    }

}
