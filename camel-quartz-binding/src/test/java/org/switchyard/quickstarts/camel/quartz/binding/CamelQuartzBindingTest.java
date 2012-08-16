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
package org.switchyard.quickstarts.camel.quartz.binding;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparisons.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparisons.lessThanOrEqualTo;

import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.mixins.CDIMixIn;

@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = {CDIMixIn.class},
    scanners = BeanSwitchYardScanner.class
)
@RunWith(SwitchYardRunner.class)
public class CamelQuartzBindingTest {

    private SwitchYardTestKit _testKit;

    @Test
    public void shouldExecuteService() throws Exception {
        final MockHandler greetingService = _testKit.registerInOnlyService("GreetingService");

        // Number of executions can vary, depends on moment when sleep will be executed
        // eg. 10:50:999, 10:51:003, 10:52:000 - we'll get three executions
        // or  10:50:000, 10:51:001 - we'll get two executions
        // however we never should get more than three triggers and less than two
        Thread.sleep(1001);

        final LinkedBlockingQueue<Exchange> recievedMessages = greetingService.getMessages();
        assertThat(recievedMessages, is(notNullValue()));
        assertThat(recievedMessages.size(), is(greaterThanOrEqualTo(1)));
    }

}
