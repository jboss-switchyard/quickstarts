/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.soap;

import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.component.soap.util.SOAPUtil;

/**
 * SOAPUtilTest
 *
 * @author Kevin Conner
 * 
 */
public class SOAPUtilTest {
    @Test
    public void testSOAP11EmptyFaultString() throws Exception {
        final SOAPFault soapFault = SOAPUtil.createFault(new Throwable(""), SOAPBinding.SOAP11HTTP_BINDING, null);

        final SOAPMessage message = SOAPUtil.generateSOAP11Fault(new SOAPFaultException(soapFault));
        Assert.assertNotNull("SOAPMessage should have been returned", message);
    }

    @Test
    public void testSOAP12EmptyFaultString() throws Exception {
        final SOAPFault soapFault = SOAPUtil.createFault(new Throwable(""), SOAPBinding.SOAP12HTTP_BINDING, null);
        
        final SOAPMessage message = SOAPUtil.generateSOAP12Fault(new SOAPFaultException(soapFault));
        Assert.assertNotNull("SOAPMessage should have been returned", message);
    }
}
