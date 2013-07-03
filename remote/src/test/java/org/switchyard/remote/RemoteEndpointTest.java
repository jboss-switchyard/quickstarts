/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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