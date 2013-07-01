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

import javax.inject.Inject;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;

@Service(ConsumerService.class)
public class ConsumerBean implements ConsumerService {
    
    @Inject @Reference
    private OneWay oneWay;
    
    @Inject @Reference
    private RequestResponse requestResponse;
    
    @Override
    public void consumeInOnlyNoArgsService() {
        oneWay.oneWayNoArgs();
    }
    
    @Override
    public void consumeInOnlyService(Object message) {
        oneWay.oneWay(message);
    }
    
    @Override
    public Object consumeInOutService(Object message) throws ConsumerException {
        try {
            Object reply = null;
            reply = requestResponse.reply(message);
            Assert.assertEquals(message, reply);
            return reply;
        } catch (ConsumerException e) {
            Assert.assertEquals(message, e);
            // OK... this validates that the remote exception was transported through the
            // Exchange fault mechanism and then rethrown by the client proxy.  Create
            // and throw a new exception...
            throw new ConsumerException("remote-exception-received");
        }
    }

    @Override
    public Object consumeInOutServiceThrowsRuntimeException(Object message) {
        try {
            Object reply = null;
            reply = requestResponse.reply(message);
            Assert.assertEquals(message, reply);
            return reply;
        } catch (ConsumerException e) {
            Assert.assertEquals(message, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String domOperation(Document message) {
        try {
            XMLUnit.compareXML(XMLHelper.getDocument(new InputSource(new StringReader("<a><b/></a>"))), message);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        return "<c/>";
    }
}
