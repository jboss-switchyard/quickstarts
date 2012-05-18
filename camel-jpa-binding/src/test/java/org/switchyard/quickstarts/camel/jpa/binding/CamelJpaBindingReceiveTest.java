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
package org.switchyard.quickstarts.camel.jpa.binding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.PreparedStatement;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.transaction.TransactionMixIn;
import org.switchyard.quickstarts.camel.jpa.binding.domain.Greet;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;

@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML, 
    mixins = {CDIMixIn.class, TransactionMixIn.class},
    scanners = BeanSwitchYardScanner.class)
@RunWith(SwitchYardRunner.class)
public class CamelJpaBindingReceiveTest extends CamelJpaBindingTest {

    private SwitchYardTestKit _testKit;
    private MockHandler service;

    @Test
    public void receiveEntity() throws Exception {
        _testKit.removeService("GreetingService");
        _testKit.removeService("StoreService");
        service = _testKit.registerInOnlyService("GreetingService");

        PreparedStatement statement = connection.prepareStatement("insert into events (createdAt,sender,receiver) values(now(), ?, ?)");
        statement.setString(1, SENDER);
        statement.setString(2, RECEIVER);
        statement.execute();

        service.waitForOKMessage();

        final LinkedBlockingQueue<Exchange> receivedMessages = service.getMessages();
        assertNotNull(receivedMessages);

        final Exchange recievedExchange = receivedMessages.iterator().next();
        Greet receiver = recievedExchange.getMessage().getContent(Greet.class);

        assertEquals(SENDER, receiver.getSender());
        assertEquals(RECEIVER, receiver.getReceiver());
    }

}
