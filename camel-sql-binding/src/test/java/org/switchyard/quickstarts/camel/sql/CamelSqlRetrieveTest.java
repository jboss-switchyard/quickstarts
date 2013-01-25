/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.quickstarts.camel.sql;

import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.switchyard.Exchange;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.quickstarts.camel.sql.binding.Greeting;
import org.switchyard.quickstarts.camel.sql.binding.GreetingService;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = {CDIMixIn.class},
    scanners = {BeanSwitchYardScanner.class, TransformSwitchYardScanner.class}
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
