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
    mixins = {CDIMixIn.class},
    scanners = TransformSwitchYardScanner.class
)
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
