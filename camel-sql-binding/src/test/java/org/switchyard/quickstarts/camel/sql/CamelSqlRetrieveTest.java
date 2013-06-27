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
package org.switchyard.quickstarts.camel.sql;

import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    mixins = {CDIMixIn.class},
    scanners = TransformSwitchYardScanner.class
)
public class CamelSqlRetrieveTest extends CamelSqlBindingTest {

    private SwitchYardTestKit _testKit;

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
        List<Greeting> content = getContents(handler);
        assertEquals(1, content.size());
        assertEquals(SENDER, content.get(0).getSender());
        assertEquals(RECEIVER, content.get(0).getReceiver());
    }

    // method which is capable to hold execution of test until some records pulled from database
    private List<Greeting> getContents(MockHandler handler) {
        handler.waitForOKMessage(); // first execution of poll done
        List<Greeting> greetings = new ArrayList<Greeting>();

        while (greetings.isEmpty()) {
            for (Exchange exchange : handler.getMessages()) {
                Greeting[] content = exchange.getMessage().getContent(Greeting[].class);
                if (content != null) {
                    greetings.addAll(Arrays.asList(content));
                }
            }
        }
        return greetings;
    }

}
