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
package org.switchyard.quickstarts.camel.jpa.binding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.PreparedStatement;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.camel.component.quartz.QuartzComponent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.transaction.TransactionMixIn;
import org.switchyard.quickstarts.camel.jpa.binding.domain.Greet;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;

@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = { CDIMixIn.class, TransactionMixIn.class })
@RunWith(SwitchYardRunner.class)
public class CamelJpaBindingReceiveTest extends CamelJpaBindingTest {

    private SwitchYardTestKit _testKit;
    private MockHandler service;
    private SwitchYardCamelContext _context;

    @Before
    public void startUp() throws Exception {
        _context.getComponent("quartz", QuartzComponent.class).getScheduler().pauseAll();
    }

    @Test
    public void receiveEntity() throws Exception {
        _testKit.removeService("GreetingService");
        _testKit.removeService("StoreReference");
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
