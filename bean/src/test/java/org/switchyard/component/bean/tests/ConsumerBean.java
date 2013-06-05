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
