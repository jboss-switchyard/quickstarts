/*
/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.jca.deploy;

import javax.resource.cci.MappedRecord;
import javax.resource.cci.Record;
import javax.resource.cci.RecordFactory;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.jca.JCAMixIn;
import org.switchyard.component.test.mixins.jca.MockRecordFactory;
import org.switchyard.component.test.mixins.jca.MockResourceAdapter;
import org.switchyard.component.test.mixins.jca.ResourceAdapterConfig;

/**
 * Functional test for {@link JCAActivator}.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "switchyard-inbound-cci-test.xml", mixins = {CDIMixIn.class, JCAMixIn.class})
public class JCACCIServiceBindingTest  {
    
    private static final String ADAPTER_NAME = "myeis-ra.rar";
    private static final String JNDI_CONNECTION_FACTORY = "java:jboss/MyEISConnectionFactory";
    private static final String MCF_CLASS = "org.switchyard.component.test.mixins.jca.MockManagedConnectionFactory";
    
    private SwitchYardTestKit _testKit;
    private JCAMixIn _jcaMixIn;
    private MockResourceAdapter _adapter;
    private RecordFactory _recordFactory;
    
    @BeforeDeploy
    public void before() {
        ResourceAdapterConfig ra = new ResourceAdapterConfig(ResourceAdapterConfig.ResourceAdapterType.MOCK)
                                                .setName(ADAPTER_NAME)
                                                .addConnectionDefinition(JNDI_CONNECTION_FACTORY, MCF_CLASS);
        _jcaMixIn.deployResourceAdapters(ra);
        _adapter = (MockResourceAdapter) _jcaMixIn.getResourceAdapter(ADAPTER_NAME);
        _recordFactory = new MockRecordFactory();
    }

    @Test
    public void testInflowCCI() throws Exception {
        _testKit.removeService("JCACCIService");
        final MockHandler mockHandler = _testKit.registerInOutService("JCACCIService");
        mockHandler.forwardInToOut();

        _adapter.fireCreateEndpoint();
        MappedRecord input = _recordFactory.createMappedRecord("testInflowCCI input");
        input.put("input", "Hello World!");
        Record result = _adapter.fireDelivery(input);
        _adapter.fireRelease();
        
        Assert.assertEquals(1, mockHandler.getMessages().size());
        Assert.assertTrue(result instanceof MappedRecord);
        Assert.assertEquals("Hello World!", ((MappedRecord)result).get("input"));
    }
    
    @Test
    public void testInflowCCIWithBeforeAfterDelivery() throws Exception {
        _testKit.removeService("JCACCIService");
        final MockHandler mockHandler = _testKit.registerInOutService("JCACCIService");
        mockHandler.forwardInToOut();

        _adapter.fireCreateEndpoint();
        _adapter.fireBeforeDelivery();
        MappedRecord input1 = _recordFactory.createMappedRecord("testInflowCCIWithBeforeAfterDelivery input1");
        input1.put("input", "Hello1");
        Record result1 = _adapter.fireDelivery(input1);
        _adapter.fireAfterDelivery();
        
        Assert.assertEquals(1, mockHandler.getMessages().size());
        Assert.assertTrue(result1 instanceof MappedRecord);
        Assert.assertEquals("Hello1", ((MappedRecord)result1).get("input"));
    }

    @Test
    public void testInflowCCIReuseReleasedEndpoint() throws Exception {
        _testKit.removeService("JCACCIService");
        final MockHandler mockHandler = _testKit.registerInOutService("JCACCIService");
        mockHandler.forwardInToOut();

        _adapter.fireCreateEndpoint();

        MappedRecord input1 = _recordFactory.createMappedRecord("testInflowCCIReuseReleasedEndpoint input1");
        input1.put("input", "Hello1");
        Record result1 = _adapter.fireDelivery(input1);

        Assert.assertEquals(1, mockHandler.getMessages().size());
        Assert.assertTrue(result1 instanceof MappedRecord);
        Assert.assertEquals("Hello1", ((MappedRecord)result1).get("input"));

        _adapter.fireRelease();
        
        MappedRecord input2 = _recordFactory.createMappedRecord("testInflowCCIReuseReleasedEndpoint input2");
        input2.put("input", "Hello2");
        Record result2 = _adapter.fireDelivery(input2);
        
        Assert.assertEquals(2, mockHandler.getMessages().size());
        Assert.assertTrue(result2 instanceof MappedRecord);
        Assert.assertEquals("Hello2", ((MappedRecord)result2).get("input"));
        
    }

    @Test(expected = RuntimeException.class)
    public void testInflowCCIErrorMultipleDelivery() throws Exception {
        final MockHandler mockHandler = _testKit.registerInOutService("JCACCIService");
        mockHandler.forwardInToOut();

        _adapter.fireCreateEndpoint();
        _adapter.fireBeforeDelivery();
        MappedRecord input1 = _recordFactory.createMappedRecord("testInflowCCIErrorMultipleDelivery input1");
        input1.put("input", "Hello1");
        MappedRecord input2 = _recordFactory.createMappedRecord("testInflowCCIErrorMultipleDelivery input2");
        input2.put("input", "Hello2");
        Record result1 = _adapter.fireDelivery(input1);
        Record result2 = _adapter.fireDelivery(input2);
    }
    
    @Test(expected = RuntimeException.class)
    public void testInflowCCIErrorAfterDeliveryWithoutBefore() throws Exception {
        final MockHandler mockHandler = _testKit.registerInOutService("JCACCIService");
        mockHandler.forwardInToOut();

        _adapter.fireCreateEndpoint();
        MappedRecord input1 = _recordFactory.createMappedRecord("testInflowCCIErrorAfterDeliveryWithoutBefore input1");
        input1.put("input", "Hello1");
        Record result1 = _adapter.fireDelivery(input1);
        _adapter.fireAfterDelivery();
    }

    @Test(expected = RuntimeException.class)
    public void testInflowCCIErrorBeforeDeliveryWithoutPreviousAfter() throws Exception {
        final MockHandler mockHandler = _testKit.registerInOutService("JCACCIService");
        mockHandler.forwardInToOut();

        _adapter.fireCreateEndpoint();
        _adapter.fireBeforeDelivery();
        MappedRecord input1 = _recordFactory.createMappedRecord("testInflowCCIErrorBeforeDeliveryWithoutPreviousAfter input1");
        input1.put("input", "Hello1");
        Record result1 = _adapter.fireDelivery(input1);
        
        _adapter.fireBeforeDelivery();
    }
}

