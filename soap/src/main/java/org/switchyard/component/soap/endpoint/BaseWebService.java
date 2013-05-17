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
 
package org.switchyard.component.soap.endpoint;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingType;
import javax.xml.ws.Provider;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPBinding;

import org.switchyard.common.type.Classes;
import org.switchyard.component.soap.InboundHandler;

/**
 * This is the abstract base class for a SOAP messages.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
@WebServiceProvider
@ServiceMode(Mode.MESSAGE)
@BindingType(SOAPBinding.SOAP12HTTP_BINDING) // Actually accepts both 1.1 and 1.2; needs to be present for ws-security to work.
@HandlerChain(file="jaxws-handlers.xml")
public class BaseWebService implements Provider<SOAPMessage> {

    private static String ACTION_EQUALS = "action=";
    private static String CONTENT_TYPE_L = "Content-type";
    private static String CONTENT_TYPE = "Content-Type";

    private InboundHandler _serviceConsumer;
    private ClassLoader _invocationClassLoader;

    @Resource
    private WebServiceContext _wsContext;

    /**
     * Contruct a generic Webservice.
     */
    public BaseWebService() {
    }

    /**
     * Sets the service handler.
     * @param serviceConsumer the service handler.
     */
    public void setConsumer(final InboundHandler serviceConsumer) {
        _serviceConsumer = serviceConsumer;
    }

    /**
     * Sets the Invocation TCCL.
     * @param classLoader the classloader to be set.
     */
    public void setInvocationClassLoader(final ClassLoader classLoader) {
        _invocationClassLoader = classLoader;
    }

    /**
     * The Webservice implementation method, invokes the service handler.
     * @param request the SOAP request
     * @return the SOAP response
     */
    public SOAPMessage invoke(final SOAPMessage request) {
        if (request != null) {
            // Workaround for SOAP1.2 SOAPAction. Although HTTP header has the action value it is missing in
            // the SOAPMessage's MimeHeader
            MimeHeaders mimeHeaders = request.getMimeHeaders();
            String[] mimeContentTypes = mimeHeaders.getHeader(CONTENT_TYPE);
            if (mimeContentTypes != null)  {
                String mimeContentType = mimeContentTypes[0];
                if ((mimeContentType != null) && (mimeContentType.indexOf(ACTION_EQUALS) == -1)) {
                    Map headers = (Map)_wsContext.getMessageContext().get(MessageContext.HTTP_REQUEST_HEADERS);
                    List<String> contentTypes = (List)headers.get(CONTENT_TYPE_L);
                    if ((contentTypes == null) || (contentTypes.size() == 0)) {
                        contentTypes = (List)headers.get(CONTENT_TYPE);
                    }
                    if ((contentTypes != null) && (contentTypes.size() > 0)) {
                        int idx =  contentTypes.get(0).indexOf(ACTION_EQUALS);
                        if (idx > 0) {
                            String action = contentTypes.get(0).substring(idx + 7).replace("\"\"","\"");
                            mimeHeaders.setHeader(CONTENT_TYPE, mimeContentType + "; " + ACTION_EQUALS + action);
                        }
                    }
                }
            }
        }

        SOAPMessage response = null;
        ClassLoader original = Classes.setTCCL(_invocationClassLoader);
        try {
            response = _serviceConsumer.invoke(request, _wsContext);
        } finally {
            Classes.setTCCL(original);
        }
        return response;
    }
}

