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
package org.switchyard.remote;

import java.io.ByteArrayOutputStream;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.extensions.java.JavaService;
import org.switchyard.serial.FormatType;
import org.switchyard.serial.Serializer;
import org.switchyard.serial.SerializerFactory;

public class RemoteEndpointTest {
    
    private Serializer _serializer;
    private ByteArrayOutputStream _sink;
    
    @Before
    public void setUp() throws Exception {
        _serializer = SerializerFactory.create(FormatType.JSON);
        _sink = new ByteArrayOutputStream();
    }

    @Test
    public void emptyService() throws Exception {
        RemoteEndpoint ep1 = new RemoteEndpoint();
        _serializer.serialize(ep1, RemoteEndpoint.class, _sink);
        RemoteEndpoint ep2 = _serializer.deserialize(_sink.toByteArray(), RemoteEndpoint.class);
        Assert.assertNotNull(ep2);
    }
    
    @Test
    public void serviceAndDomainAndEndpoint() throws Exception {
        RemoteEndpoint ep1 = new RemoteEndpoint(new QName("foo"), new QName("xyz"), "abc", "bar", null);
        _serializer.serialize(ep1, RemoteEndpoint.class, _sink);
        RemoteEndpoint ep2 = _serializer.deserialize(_sink.toByteArray(), RemoteEndpoint.class);
        Assert.assertEquals(ep1.getServiceName(), ep2.getServiceName());
        Assert.assertEquals(ep1.getNode(), ep2.getNode());
        Assert.assertEquals(ep1.getDomainName(), ep2.getDomainName());
        Assert.assertEquals(ep1.getEndpoint(), ep2.getEndpoint());
    }
    
    @Test
    public void withJavaContract() throws Exception {
        RemoteInterface contract = RemoteInterface.fromInterface(JavaService.fromClass(MyContract.class));
        RemoteEndpoint ep1 = new RemoteEndpoint(new QName("foo"), new QName("xyz"), "abc", "bar", contract);
        _serializer.serialize(ep1, RemoteEndpoint.class, _sink);
        RemoteEndpoint ep2 = _serializer.deserialize(_sink.toByteArray(), RemoteEndpoint.class);
        Assert.assertEquals(ep1.getServiceName(), ep2.getServiceName());
        Assert.assertEquals(contract.getOperation("submit").getInputType(), 
                ep2.getContract().getOperation("submit").getInputType());
        Assert.assertEquals(contract.getOperation("reply").getOutputType(), 
                ep2.getContract().getOperation("reply").getOutputType());
    }
}

interface MyContract {
    void submit(Foo foo);
    Bar reply(Foo foo);
}

class Foo { }

class Bar { }