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
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;

/**
 * SQL binding test - checks insert and retrieve operation.
 * 
 * @author Lukasz Dywicki
 */
@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = {CDIMixIn.class},
    scanners = BeanSwitchYardScanner.class
)
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
