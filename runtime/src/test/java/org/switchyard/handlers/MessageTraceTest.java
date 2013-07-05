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

package org.switchyard.handlers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.MockDomain;
import org.switchyard.MockHandler;
import org.switchyard.ServiceReference;
import org.switchyard.transform.BaseTransformer;

public class MessageTraceTest {

    private MockDomain _domain;
    
    @Before
    public void setUp() throws Exception {
        _domain = new MockDomain();
    }
    
    @Test
    public void testInMessageTrace() {
        ServiceReference service = _domain.createInOnlyService(new QName("InTrace"));
        Exchange exchange = service.createExchange();
        exchange.send(exchange.createMessage());
    }
    
    @Test
    public void testInOutMessageTrace() throws Exception {
        ServiceReference service = _domain.createInOutService(
                new QName("InOutTrace"), new MockHandler().forwardInToOut());
        Exchange exchange = service.createExchange(new MockHandler());
        exchange.send(exchange.createMessage());
    }

    @Test
    public void testInFaultMessageTrace() throws Exception {
        ServiceReference service = _domain.createInOutService(
                new QName("InFaultTrace"), new MockHandler().forwardInToOut());
        Exchange exchange = service.createExchange(new MockHandler());
        exchange.send(exchange.createMessage());
    }
    
    @Test
    public void testStreamContent() throws Exception {
        String contentAsString = "abc-InputStream-xyz";
        MockHandler provider = new MockHandler();
        _domain.getTransformerRegistry().addTransformer(new StreamTransformer());
        ServiceReference service = _domain.createInOnlyService(new QName("StreamTest"), provider);
        
        InputStream contentAsStream = new ByteArrayInputStream(contentAsString.getBytes());
        Exchange ex = service.createExchange();
        Message msg = ex.createMessage();
        msg.setContent(contentAsStream);
        ex.send(msg);

        Message rcvdMsg = provider.getMessages().poll().getMessage();
        Assert.assertTrue(rcvdMsg.getContent(InputStream.class).available() > 0);
        
    }
    
    @Test
    public void testReaderContent() throws Exception {
        String contentAsString = "abc-Reader-xyz";
        MockHandler provider = new MockHandler();
        _domain.getTransformerRegistry().addTransformer(new ReaderTransformer());
        ServiceReference service = _domain.createInOnlyService(new QName("ReaderTest"), provider);
        
        StringReader contentAsReader = new StringReader(contentAsString);
        Exchange ex = service.createExchange();
        Message msg = ex.createMessage();
        msg.setContent(contentAsReader);
        ex.send(msg);

        Message rcvdMsg = provider.getMessages().poll().getMessage();
        Assert.assertTrue(rcvdMsg.getContent(Reader.class).read() != -1);
    }
}

class StreamTransformer extends BaseTransformer<InputStream, String> {
    public String transform(InputStream stream) {
        try {
            byte[] buf = new byte[500];
            int count = stream.read(buf);
            return new String(buf, 0, count);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

class ReaderTransformer extends BaseTransformer<Reader, String> {
    public String transform(Reader reader) {
        try {
            char[] buf = new char[500];
            int count = reader.read(buf);
            return new String(buf, 0, count);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
