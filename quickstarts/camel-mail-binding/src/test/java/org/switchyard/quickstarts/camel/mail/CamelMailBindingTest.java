/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.camel.mail;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;

/**
 * SQL binding test - checks insert and retrieve operation.
 * 
 * @author Lukasz Dywicki
 */
@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = { CDIMixIn.class })
@RunWith(SwitchYardRunner.class)
public class CamelMailBindingTest {

    private static final String PAYLOAD = "Community";
    private static final String SUBJECT = "Hello";

    private Session session;

    private Folder inbox;
    private Folder read;
    private Folder riderInbox;

    @Before
    public void before() throws Exception {
        session = Session.getInstance(new Properties());

        Store localStore = session.getStore("imap");
        localStore.connect("localhost", "camel", "dummy");

        Store riderStore = session.getStore("imap");
        riderStore.connect("camel", "rider", "dummy");

        inbox = localStore.getDefaultFolder();
        inbox.open(Folder.READ_ONLY);
        read = localStore.getFolder("after-processing");
        read.open(Folder.READ_ONLY);

        riderInbox = riderStore.getDefaultFolder();
        riderInbox.open(Folder.READ_ONLY);
    }

    @Test
    public void shouldReceiveMessageAndSendOutgoingMail() throws Exception {
        // send test message to camel@localhost
        MimeMessage msg = new MimeMessage(session);
        msg.setSubject(SUBJECT);
        msg.setRecipients(RecipientType.TO, "camel@localhost");
        msg.setText(PAYLOAD);

        Transport.send(msg);
        Thread.sleep(500);

        Message[] messages = inbox.getMessages();
        // verify mailbox states, note that mock javamail do not create new
        // folders, so everything goes to same place
        assertEquals(2, messages.length);

        messages = read.getMessages();
        assertEquals(2, messages.length);
        assertEquals(PAYLOAD, messages[0].getContent());
        assertEquals(SUBJECT, messages[0].getSubject());

        messages = riderInbox.getMessages();
        assertEquals(1, messages.length);
        assertEquals("Forwarded message", messages[0].getSubject());
        assertEquals(PAYLOAD, messages[0].getContent());
    }

}
