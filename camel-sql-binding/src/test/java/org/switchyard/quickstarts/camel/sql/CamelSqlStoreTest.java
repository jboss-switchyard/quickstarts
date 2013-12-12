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
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;

import org.apache.camel.component.quartz.QuartzComponent;
import org.apache.camel.component.timer.TimerComponent;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.quickstarts.camel.sql.binding.Greeting;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = { CDIMixIn.class },
    scanners = TransformSwitchYardScanner.class)
public class CamelSqlStoreTest extends CamelSqlBindingTest {

    private SwitchYardCamelContext _context;

    @ServiceOperation("GreetingService")
    protected Invoker invoker;

    /**
     * Suspend quartz polling.
     * 
     * @throws Exception Anything.
     */
    @Before
    public void before() throws Exception {
        _context.getComponent("quartz", QuartzComponent.class).getScheduler().pauseAll();
        _context.getComponent("timer", TimerComponent.class).stop();
    }

    @Test
    public void shouldStoreGreet() throws Exception {
        invoker.operation("store").sendInOnly(new Greeting(RECEIVER, SENDER));

        ResultSet result = connection.createStatement().executeQuery("SELECT * FROM greetings");
        assertTrue(result.next());
        assertEquals(RECEIVER, result.getString("receiver"));
        assertEquals(SENDER, result.getString("sender"));
        result.close();
    }

}
