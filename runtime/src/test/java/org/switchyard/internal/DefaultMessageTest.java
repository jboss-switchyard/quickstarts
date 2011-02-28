/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
