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
package org.switchyard.component.bean.inject;

import javax.activation.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.component.common.SynchronousInOutHandler;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.Invoker;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.TestDataSource;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class)
public class InjectTest {

    private SwitchYardTestKit _testKit;

    @Test
    public void testInject() {
        Invoker invoker = _testKit.newInvoker("InjectService.doSomething");
        SynchronousInOutHandler handler = new SynchronousInOutHandler();
        Exchange exchange = invoker.createExchange(handler);
        Message message = exchange.createMessage();
        Context context = exchange.getContext(message);
        context.setProperty("someProp", "somePropVal");
        message.setContent("blah");
        DataSource attach = new TestDataSource("someAttach", "text/plain", "someAttachData");
        message.addAttachment(attach.getName(), attach);
        exchange.send(message);
        Exchange outExchange = handler.waitForOut();
        Assert.assertEquals("true, true, true", outExchange.getMessage().getContent());
    }

}
