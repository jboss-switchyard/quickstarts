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

package org.switchyard.component.bean.tests;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Message;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;

/*
 * Assorted methods for testing a CDI bean providing a service in SwitchYard.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class)
public class BeanProviderTest {

    @ServiceOperation("OneWay.oneWay")
    private Invoker oneWay;
    @ServiceOperation("RequestResponse.reply")
    private Invoker reply;

    @Test
    public void invokeOneWayProviderWithInOnly() {
        oneWay.sendInOnly("hello");
    }
    
    @Test
    public void invokeRequestResponseProviderWithInOut() {
        String ECHO_MSG = "hello";
        Message response = reply.sendInOut(ECHO_MSG);

        Assert.assertEquals(ECHO_MSG, response.getContent());
    }
}
