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
package org.switchyard.test.jca;

import java.io.IOException;
import java.net.URL;

import javax.naming.InitialContext;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.Record;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.common.type.Classes;
import org.switchyard.test.jca.mockra.MockRecordFactory;
import org.switchyard.test.jca.mockra.MockResourceAdapter;
import org.switchyard.test.jca.mockra.MockResourceAdapterUtil;

/**
 * Functional test for JCA CCI Service binding.
 */
@RunWith(Arquillian.class)
public class JCACCIServiceBindingTest  {

    private static final String TEST_CONFIG = "org/switchyard/test/jca/switchyard-inbound-cci-test.xml";
    private static final String DEPLOYMENT_STRUCTURE = "org/switchyard/test/jca/cci-test-app-deployment-structure.xml";
    private static final String APP_NAME = "switchyard-JCACCIServiceBindingTest.jar";

    private MockResourceAdapter _adapter;
    private MockRecordFactory _recordFactory;

    @ArquillianResource
    private InitialContext _context;
    
    @Deployment(order = 1, name = MockResourceAdapterUtil.ADAPTER_ARCHIVE_NAME)
    public static ResourceAdapterArchive createResourceAdapter() throws IOException {
        return MockResourceAdapterUtil.createMockResourceAdapterArchive();
    }

    @Deployment(order = 2)
    public static JavaArchive createDeployment() throws Exception {
        URL testConfigUrl = Classes.getResource(TEST_CONFIG);
        URL deploymentStructureUrl = Classes.getResource(DEPLOYMENT_STRUCTURE);
        String beansXml = Descriptors.create(BeansDescriptor.class).exportAsString();
        return ShrinkWrap.create(JavaArchive.class, APP_NAME)
                         .addClass(JCACCIService.class)
                         .addClass(JCACCIServiceImpl.class)
                         .addAsManifestResource(new UrlAsset(deploymentStructureUrl), "jboss-deployment-structure.xml")
                         .addAsManifestResource(new UrlAsset(testConfigUrl), "switchyard.xml")
                         .addAsManifestResource(new StringAsset(beansXml), "beans.xml");
    }

    @Before
    public void before() {
        try {
            _recordFactory = new MockRecordFactory();
            _adapter = (MockResourceAdapter) _context.lookup(MockResourceAdapterUtil.JNDI_ADAPTER);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testInflowCCI() throws Exception {
        _adapter.fireCreateEndpoint();
        MappedRecord input = _recordFactory.createMappedRecord("testInflowCCI input");
        input.put("input", "World");
        Record result = _adapter.fireDelivery(input);
        _adapter.fireRelease();
        
        Assert.assertTrue(result instanceof MappedRecord);
        Assert.assertEquals("Hello World !", ((MappedRecord)result).get("input"));
    }
    
    @Test
    public void testInflowCCIWithBeforeAfterDelivery() throws Exception {
        _adapter.fireCreateEndpoint();
        _adapter.fireBeforeDelivery();
        MappedRecord input1 = _recordFactory.createMappedRecord("testInflowCCIWithBeforeAfterDelivery input1");
        input1.put("input", "World2");
        Record result1 = _adapter.fireDelivery(input1);
        _adapter.fireAfterDelivery();
        
        Assert.assertTrue(result1 instanceof MappedRecord);
        Assert.assertEquals("Hello World2 !", ((MappedRecord)result1).get("input"));
    }

    @Test
    public void testInflowCCIReuseReleasedEndpoint() throws Exception {
        _adapter.fireCreateEndpoint();
        MappedRecord input1 = _recordFactory.createMappedRecord("testInflowCCIReuseReleasedEndpoint input1");
        input1.put("input", "World3");
        Record result1 = _adapter.fireDelivery(input1);
        Assert.assertTrue(result1 instanceof MappedRecord);
        Assert.assertEquals("Hello World3 !", ((MappedRecord)result1).get("input"));
        _adapter.fireRelease();
        
        MappedRecord input2 = _recordFactory.createMappedRecord("testInflowCCIReuseReleasedEndpoint input2");
        input2.put("input", "World4");
        Record result2 = _adapter.fireDelivery(input2);
        Assert.assertTrue(result2 instanceof MappedRecord);
        Assert.assertEquals("Hello World4 !", ((MappedRecord)result2).get("input"));
    }

    @Test(expected = RuntimeException.class)
    public void testInflowCCIErrorMultipleDelivery() throws Exception {
        _adapter.fireCreateEndpoint();
        _adapter.fireBeforeDelivery();
        MappedRecord input1 = _recordFactory.createMappedRecord("testInflowCCIErrorMultipleDelivery input1");
        input1.put("input", "World5");
        MappedRecord input2 = _recordFactory.createMappedRecord("testInflowCCIErrorMultipleDelivery input2");
        input2.put("input", "World6");
        Record result1 = _adapter.fireDelivery(input1);
        Record result2 = _adapter.fireDelivery(input2);
    }
    
    @Test(expected = RuntimeException.class)
    public void testInflowCCIErrorAfterDeliveryWithoutBefore() throws Exception {
        _adapter.fireCreateEndpoint();
        MappedRecord input1 = _recordFactory.createMappedRecord("testInflowCCIErrorAfterDeliveryWithoutBefore input1");
        input1.put("input", "World7");
        Record result1 = _adapter.fireDelivery(input1);
        _adapter.fireAfterDelivery();
    }

    @Test(expected = RuntimeException.class)
    public void testInflowCCIErrorBeforeDeliveryWithoutPreviousAfter() throws Exception {
        _adapter.fireCreateEndpoint();
        _adapter.fireBeforeDelivery();
        MappedRecord input1 = _recordFactory.createMappedRecord("testInflowCCIErrorBeforeDeliveryWithoutPreviousAfter input1");
        input1.put("input", "World8");
        Record result1 = _adapter.fireDelivery(input1);
        
        _adapter.fireBeforeDelivery();
    }
}

