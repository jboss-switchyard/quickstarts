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
import javax.resource.cci.InteractionSpec;
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
import org.switchyard.test.jca.mockra.InteractionListener;
import org.switchyard.test.jca.mockra.MockConnectionFactory;
import org.switchyard.test.jca.mockra.MockRecordFactory;
import org.switchyard.test.jca.mockra.MockResourceAdapter;
import org.switchyard.test.jca.mockra.MockResourceAdapterUtil;

/**
 * Functional test for JCA CCI Reference binding.
 */
@RunWith(Arquillian.class)
public class JCACCIReferenceBindingTest  {

    private static final String TEST_CONFIG = "org/switchyard/test/jca/switchyard-outbound-cci-test.xml";
    private static final String DEPLOYMENT_STRUCTURE = "org/switchyard/test/jca/cci-test-app-deployment-structure.xml";
    private static final String JNDI_PROPERTIES = "org/switchyard/test/jca/outbound-jms-jndi.properties";
    private static final String APP_NAME = "switchyard-JCACCIReferenceBindingTest.jar";

    private MockResourceAdapter _adapter;
    private MockRecordFactory _recordFactory;

    @ArquillianResource
    InitialContext _context;

    @Deployment(order = 1, name = MockResourceAdapterUtil.ADAPTER_ARCHIVE_NAME)
    public static ResourceAdapterArchive createResourceAdapter() throws IOException {
        return MockResourceAdapterUtil.createMockResourceAdapterArchive();
    }

    @Deployment(order = 2)
    public static JavaArchive createDeployment() throws Exception {
        URL testConfigUrl = Classes.getResource(TEST_CONFIG);
        URL deploymentStructureUrl = Classes.getResource(DEPLOYMENT_STRUCTURE);
        URL jndiProperties = Classes.getResource(JNDI_PROPERTIES);
        String beansXml = Descriptors.create(BeansDescriptor.class).exportAsString();
        return ShrinkWrap.create(JavaArchive.class, APP_NAME)
                         .addClass(JCACCIReference.class)
                         .addClass(JCACCIReferenceService.class)
                         .addClass(JCACCIReferenceServiceImpl.class)
                         .addAsResource(new UrlAsset(jndiProperties), "jndi.properties")
                         .addAsManifestResource(new UrlAsset(deploymentStructureUrl), "jboss-deployment-structure.xml")
                         .addAsManifestResource(new UrlAsset(testConfigUrl), "switchyard.xml")
                         .addAsManifestResource(new StringAsset(beansXml), "beans.xml");
    }

    @Before
    public void before() {
        try {
            _recordFactory = new MockRecordFactory();
            MockConnectionFactory factory = (MockConnectionFactory) _context.lookup(MockResourceAdapterUtil.JNDI_CONNECTION_FACTORY);
            factory.setInteractionListener(new InteractionListener() {
                public Record onExecute(InteractionSpec spec, Record input) {
                    MappedRecord mapped = (MappedRecord) input;
                    mapped.put("name", "Hello, " + mapped.get("name") + "!");
                    return mapped;
                }
                public boolean onExecute(InteractionSpec spec, Record input, Record output) {return true;}
            });
            _adapter = (MockResourceAdapter) _context.lookup(MockResourceAdapterUtil.JNDI_ADAPTER);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testOutboundCCI() throws Exception {
        _adapter.fireCreateEndpoint();
        MappedRecord input = _recordFactory.createMappedRecord("testOutboundCCI input");
        input.put("name", "Uragasumi");
        Record result = _adapter.fireDelivery(input);
        _adapter.fireRelease();
        Assert.assertTrue(result instanceof MappedRecord);
        Assert.assertEquals("Hello, Uragasumi!", MappedRecord.class.cast(result).get("name"));
    }
}

