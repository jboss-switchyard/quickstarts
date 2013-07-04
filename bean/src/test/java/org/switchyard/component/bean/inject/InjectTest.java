/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
