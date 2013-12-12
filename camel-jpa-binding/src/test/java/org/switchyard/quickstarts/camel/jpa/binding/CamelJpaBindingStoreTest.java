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
import static org.junit.Assert.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import org.apache.camel.component.quartz.QuartzComponent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.transaction.TransactionMixIn;
import org.switchyard.quickstarts.camel.jpa.binding.domain.Greet;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;

@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = { CDIMixIn.class, TransactionMixIn.class })
@RunWith(SwitchYardRunner.class)
public class CamelJpaBindingStoreTest extends CamelJpaBindingTest {

    private SwitchYardTestKit _testKit;

    private SwitchYardCamelContext _context;

    @Before
    public void startUp() throws Exception {
        _context.getComponent("quartz", QuartzComponent.class).getScheduler().pauseAll();
    }

    @Test
    public void storeEntity() throws Exception {
        Calendar createdAt = Calendar.getInstance();

        Greet event = new Greet();
        event.setReceiver(RECEIVER);
        event.setSender(SENDER);
        event.setCreatedAt(createdAt);

        _testKit.newInvoker("StoreReference").sendInOnly(event);

        PreparedStatement statement = connection.prepareStatement("select createdAt, sender, receiver from events");
        ResultSet resultSet = statement.executeQuery();

        assertTrue(resultSet.next());
        assertEquals(SENDER, resultSet.getString("sender"));
        assertEquals(RECEIVER, resultSet.getString("receiver"));
        assertEquals(createdAt.getTimeInMillis(), resultSet.getTimestamp("createdAt").getTime());
    }

}
