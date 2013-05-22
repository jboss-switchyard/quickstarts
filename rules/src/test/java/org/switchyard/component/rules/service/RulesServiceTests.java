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
package org.switchyard.component.rules.service;

import javax.activation.DataSource;
import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.component.common.knowledge.annotation.Input;
import org.switchyard.component.common.knowledge.annotation.Manifest;
import org.switchyard.component.common.knowledge.annotation.Resource;
import org.switchyard.component.rules.annotation.Execute;
import org.switchyard.component.rules.annotation.Rules;
import org.switchyard.component.rules.config.model.RulesComponentImplementationModel;
import org.switchyard.component.rules.config.model.RulesSwitchYardScanner;
import org.switchyard.component.rules.exchange.RulesExchangeHandler;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.TestDataSource;

/**
 * Tests the Rules implementation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
@RunWith(SwitchYardRunner.class)
public class RulesServiceTests {

    private static final String ACCESS_ATTACHMENT_DRL = "org/switchyard/component/rules/service/RulesServiceTests-AccessAttachment.drl";

    private ServiceDomain serviceDomain;

    @Rules(manifest=@Manifest(resources=@Resource(location=ACCESS_ATTACHMENT_DRL, type="DRL")))
    public interface AccessAttachment {
        @Execute(inputs={
            @Input(from="message.attachmentMap['someAttach']"),
            @Input(from="message.content")
        })
        public Object process(Object content);
    }

    @Test
    public void testAccessAttachment() throws Exception {
        final Holder holder = new Holder();
        RulesComponentImplementationModel bci_model = (RulesComponentImplementationModel)new RulesSwitchYardScanner().scan(AccessAttachment.class).getImplementation();
        RulesExchangeHandler handler = new RulesExchangeHandler(bci_model, serviceDomain);
        Service aaService = serviceDomain.registerService(new QName("AccessAttachment"), JavaService.fromClass(AccessAttachment.class), handler);
        ServiceReference aaReference = serviceDomain.registerServiceReference(aaService.getName(), aaService.getInterface(), aaService.getProviderHandler());
        handler.start();
        Exchange exchange = aaReference.createExchange("process");
        Message message = exchange.createMessage();
        message.setContent(holder);
        DataSource attachment = new TestDataSource("someAttach", "text/plain", "someAttachData");
        message.addAttachment(attachment.getName(), attachment);
        exchange.send(message);
        handler.stop();
        Assert.assertEquals("someAttachData", holder.getValue());
    }

    public static final class Holder {
        private String _value;
        public String getValue() { return _value; }
        public void setValue(String value) { _value = value; }
        public String toString() { return _value; }
    }

}
