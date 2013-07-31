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
import org.switchyard.extensions.java.JavaService;
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
    private static final String DECLARE_FACTS_DRL = "org/switchyard/component/rules/service/RulesServiceTests-DeclareFacts.drl";

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
        RulesComponentImplementationModel rci_model = (RulesComponentImplementationModel)new RulesSwitchYardScanner().scan(AccessAttachment.class).getImplementation();
        QName serviceName = new QName("AccessAttachment");
        RulesExchangeHandler handler = new RulesExchangeHandler(rci_model, serviceDomain, serviceName);
        Service aaService = serviceDomain.registerService(serviceName, JavaService.fromClass(AccessAttachment.class), handler);
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

    @Rules(manifest=@Manifest(resources=@Resource(location=DECLARE_FACTS_DRL, type="DRL")))
    public interface DeclareFacts {
        @Execute(inputs={
            @Input(from="message.content")
        })
        public Object process(Object content);
    }

    @Test
    public void testDeclareFacts() throws Exception {
        final Holder holder = new Holder();
        RulesComponentImplementationModel rci_model = (RulesComponentImplementationModel)new RulesSwitchYardScanner().scan(DeclareFacts.class).getImplementation();
        QName serviceName = new QName("DeclareFacts");
        RulesExchangeHandler handler = new RulesExchangeHandler(rci_model, serviceDomain, serviceName);
        Service dfService = serviceDomain.registerService(serviceName, JavaService.fromClass(DeclareFacts.class), handler);
        ServiceReference dfReference = serviceDomain.registerServiceReference(dfService.getName(), dfService.getInterface(), dfService.getProviderHandler());
        handler.start();
        Exchange exchange = dfReference.createExchange("process");
        Message message = exchange.createMessage();
        message.setContent(holder);
        exchange.send(message);
        handler.stop();
        Assert.assertEquals("handled", holder.getValue());
    }

    public static final class Holder {
        private String _value;
        public String getValue() { return _value; }
        public void setValue(String value) { _value = value; }
        public String toString() { return _value; }
    }

}
