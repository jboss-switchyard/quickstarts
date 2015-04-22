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
package org.switchyard.quickstarts.camel.sql;

import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.switchyard.Exchange;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.extensions.java.JavaService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.quickstarts.camel.sql.binding.Greeting;
import org.switchyard.quickstarts.camel.sql.binding.GreetingService;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = { CDIMixIn.class },
    scanners = TransformSwitchYardScanner.class)
public class CamelSqlRetrieveTest extends CamelSqlBindingTest {

    private SwitchYardTestKit _testKit;

    private final static Integer REPLY_TIMEOUT_SECONDS = 4;

    protected final static String RECEIVER_2 = "John";
    protected final static String SENDER_2 = "Robert";
    @Test
    public void shouldRetrieveGreetings() throws Exception {
        _testKit.removeService("GreetingService");
        MockHandler handler = new MockHandler();

        ServiceInterface metadata = JavaService.fromClass(GreetingService.class);
        _testKit.getServiceDomain().registerService(_testKit.createQName("GreetingService"), metadata, handler);
        PreparedStatement statement = connection.prepareStatement("INSERT INTO greetings (receiver, sender) VALUES (?,?)");
        statement.setString(1, RECEIVER);
        statement.setString(2, SENDER);
        assertEquals(1, statement.executeUpdate());

        PreparedStatement statement2 = connection.prepareStatement("INSERT INTO greetings (receiver, sender) VALUES (?,?)");
        statement2.setString(1, RECEIVER_2);
        statement2.setString(2, SENDER_2);
        assertEquals(1, statement2.executeUpdate());
        Set<Greeting> greetings = getContents(handler);
        assertEquals(2, greetings.size());
        assertEquals(true, checkGreeting(RECEIVER, SENDER, greetings));
        assertEquals(true, checkGreeting(RECEIVER_2, SENDER_2, greetings));
    }

    // method which is capable to hold execution of test until some records pulled from database
    private Set<Greeting> getContents(MockHandler handler) {
        handler.waitForOKMessage(); // first execution of poll done

        long stop = System.nanoTime() + TimeUnit.SECONDS.toNanos(REPLY_TIMEOUT_SECONDS);
        Set<Greeting> greetings = new HashSet<Greeting>();

        while (System.nanoTime() < stop) {
            for (Exchange exchange : handler.getMessages()) {
                Greeting greeting = exchange.getMessage().getContent(Greeting.class);
                if (greeting != null && !greetings.contains(greeting)) {
                    greetings.add(greeting);
                }
            }
        }
        return greetings;
    }

    private boolean checkGreeting(String receiver, String sender, Collection<Greeting> greetings) {
        for (Greeting greeting : greetings) {
            if (greeting.getReceiver().equals(receiver) && greeting.getSender().equals(sender)) {
                return true;
            }
        }
        return false;
    }

}
