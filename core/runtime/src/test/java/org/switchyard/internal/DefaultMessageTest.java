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

package org.switchyard.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.activation.DataSource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.Message;

public class DefaultMessageTest {
    
    private Message _message;
    
    @Before
    public void setUp() {
        _message = new DefaultMessage();
    }
    
    
    @Test
    public void testAddAttachment() throws Exception {
        _message.addAttachment("attach1", new DummyDS("attach1", "text/xml"));
        Assert.assertNotNull(_message.getAttachment("attach1"));
    }
    
    @Test
    public void testRemoveAttachment() throws Exception {
        _message.addAttachment("attach1", new DummyDS("attach1", "text/xml"));
        Assert.assertNotNull(_message.getAttachment("attach1"));
        _message.removeAttachment("attach1");
        Assert.assertNull(_message.getAttachment("attach1"));
        
    }
    
    @Test
    public void testGetAttachmentMap() throws Exception {
        _message.addAttachment("attach1", new DummyDS("attach1", "text/xml"));
        _message.addAttachment("attach2", new DummyDS("attach1", "text/xml"));
        Map<String, DataSource> attachments = _message.getAttachmentMap();
        // make sure the attachments we added are in the map
        Assert.assertTrue(attachments.containsKey("attach1"));
        Assert.assertTrue(attachments.containsKey("attach2"));
        // make sure that modifications to the map are not reflected in the message
        // (i.e.) the returned map is not a direct reference 
        attachments.remove("attach1");
        Assert.assertNotNull(_message.getAttachment("attach1"));
    }
    
    @Test
    public void testContent() throws Exception {
        final String message = "Hello There!";
        _message.setContent(message);
        Assert.assertEquals(message, _message.getContent());
        // the following tests to make sure casting to same type works
        String content = _message.getContent(String.class);
        Assert.assertEquals(message, content);
    }

}

class DummyDS implements DataSource {
    
    private String _contentType;
    private String _name;

    DummyDS(String contentType, String name) {
        _contentType = contentType;
        _name = name;
    }
    
    @Override
    public String getContentType() {
        return _contentType;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
    
}
