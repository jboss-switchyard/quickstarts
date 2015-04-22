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

package org.switchyard.deploy.osgi.internal.soap;

import javax.xml.ws.handler.MessageContext.Scope;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.jaxws.interceptors.WebFaultOutInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.apache.cxf.ws.addressing.ContextUtils;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.ws.addressing.RelatesToType;
import org.apache.cxf.ws.addressing.soap.MAPCodec;
import org.switchyard.Context;
import org.switchyard.component.soap.util.SOAPUtil;

/**
 * Outbound Addressing handler.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class AddressingInterceptor extends AbstractSoapInterceptor {

    /**
     * Default Constructor.
     */
    public AddressingInterceptor() {
        super(Phase.PRE_PROTOCOL);
        addAfter(WebFaultOutInterceptor.class.getName());
        addBefore(MAPCodec.class.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        if (ContextUtils.isOutbound(message)) {
            AddressingProperties maps = ContextUtils.retrieveMAPs(message, false, true, false);
            WrappedMessageContext soapContext = new WrappedMessageContext(message, Scope.APPLICATION);
            if (soapContext.containsKey(SOAPUtil.SWITCHYARD_CONTEXT)) {
                Context context = (Context)soapContext.get(SOAPUtil.SWITCHYARD_CONTEXT);

                String property = (String)context.getPropertyValue(SOAPUtil.WSA_ACTION_STR);
                AttributedURIType uri = null;
                EndpointReferenceType ref = null;
                if (property != null) {
                    uri = new AttributedURIType();
                    uri.setValue(property);
                    maps.setAction(uri);
                }
                property = (String)context.getPropertyValue(SOAPUtil.WSA_FROM_STR);
                if (property != null) {
                    uri = new AttributedURIType();
                    uri.setValue(property);
                    ref = new EndpointReferenceType();
                    ref.setAddress(uri);
                    maps.setFrom(ref);
                }
                property = (String)context.getPropertyValue(SOAPUtil.WSA_TO_STR);
                if (property != null) {
                    uri = new AttributedURIType();
                    uri.setValue(property);
                    ref = new EndpointReferenceType();
                    ref.setAddress(uri);
                    maps.setTo(ref);
                }
                property = (String)context.getPropertyValue(SOAPUtil.WSA_FAULTTO_STR);
                if (property != null) {
                    uri = new AttributedURIType();
                    uri.setValue(property);
                    ref = new EndpointReferenceType();
                    ref.setAddress(uri);
                    maps.setFaultTo(ref);
                }
                property = (String)context.getPropertyValue(SOAPUtil.WSA_REPLYTO_STR);
                if (property != null) {
                    uri = new AttributedURIType();
                    uri.setValue(property);
                    ref = new EndpointReferenceType();
                    ref.setAddress(uri);
                    maps.setReplyTo(ref);
                }
                property = (String)context.getPropertyValue(SOAPUtil.WSA_RELATESTO_STR);
                if (property != null) {
                    RelatesToType relatesTo = new RelatesToType();
                    relatesTo.setValue(property);
                    maps.setRelatesTo(relatesTo);
                }
                property = (String)context.getPropertyValue(SOAPUtil.WSA_MESSAGEID_STR);
                if (property != null) {
                    uri = new AttributedURIType();
                    uri.setValue(property);
                    maps.setMessageID(uri);
                }
            }
        }
    }
}
