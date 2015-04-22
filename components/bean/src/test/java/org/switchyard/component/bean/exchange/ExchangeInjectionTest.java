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

package org.switchyard.component.bean.exchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.Invoker;
import org.switchyard.test.MockHandler;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class)
public class ExchangeInjectionTest {

    public static final String TEST_IN_ATTACHMENT = "TestInAttachment";
    public static final String TEST_OUT_ATTACHMENT = "TestOutAttachment";
    public static final String TEST_IN_CONTENT = "TestInContent";
    public static final String TEST_OUT_CONTENT = "TestOutContent";
    public static final String TEST_IN_PROPERTY = "TestInProperty";
    public static final String TEST_OUT_PROPERTY = "TestOutProperty";

    @ServiceOperation("ExchangeInjectionService")
    private Invoker invokerService;

    @ServiceOperation("ExchangeInjectionService.sendReply")
    private Invoker sendReply;
    
    @ServiceOperation("ExchangeInjectionService.attachments")
    private Invoker attachments;
    
    @Test
    public void injectionNotNull() {
        invokerService.operation("injectionNotNull").sendInOnly("injectionNotNull");
    }
    
    @Test
    public void correctMessage() {
        invokerService
            .property(TEST_IN_PROPERTY, TEST_IN_PROPERTY)
            .operation("correctMessage")
            .sendInOut(TEST_IN_CONTENT);
    }
    
    @Test
    public void sendReply() throws Exception {
        MockHandler handler = new MockHandler();
        handler.setWaitTimeout(3000);
        Exchange ex = sendReply.createExchange(handler);
        ex.send(ex.createMessage().setContent(TEST_IN_CONTENT));
        Assert.assertEquals(1, handler.waitForOKMessage().getMessages().size());
        Assert.assertEquals(TEST_OUT_CONTENT, 
                handler.getMessages().poll().getMessage().getContent());
    }
    
    @Test
    public void attachments() throws Exception {
        MockHandler handler = new MockHandler();
        handler.setWaitTimeout(3000);
        Exchange ex = attachments.createExchange(handler);
        ex.send(ex.createMessage()
                .setContent(TEST_IN_CONTENT)
                .addAttachment(TEST_IN_ATTACHMENT, new DummyDataSource(TEST_IN_ATTACHMENT)));
        Assert.assertEquals(1, handler.waitForOKMessage().getMessages().size());
        Message reply = handler.getMessages().poll().getMessage();
        DataSource attachOut = reply.getAttachment(TEST_OUT_ATTACHMENT);
        Assert.assertNotNull(attachOut);
        Assert.assertEquals(TEST_OUT_ATTACHMENT, attachOut.getName());
    }
}

class DummyDataSource implements DataSource {
    
    private String _name;
    
    public DummyDataSource(String name) {
        _name = name;
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return null;
    }
    
}
