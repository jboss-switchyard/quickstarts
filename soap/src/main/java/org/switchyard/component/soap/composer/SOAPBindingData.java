/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.soap.composer;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.switchyard.component.common.composer.SecurityBindingData;
import org.switchyard.security.credential.Credential;
import org.switchyard.security.credential.extractor.SOAPMessageCredentialExtractor;
import org.switchyard.security.credential.extractor.ServletRequestCredentialExtractor;
import org.switchyard.security.credential.extractor.WebServiceContextCredentialExtractor;

/**
 * SOAP binding data.
 * 
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class SOAPBindingData implements SecurityBindingData {

    private final SOAPMessage _soapMessage;
    private final WebServiceContext _webServiceContext;
    private SOAPFaultInfo _soapFaultInfo;

    /**
     * Constructs a new SOAP binding data with the specified SOAP message.
     * @param soapMessage the specified SOAP message.
     */
    public SOAPBindingData(SOAPMessage soapMessage) {
        this(soapMessage, null);
    }

    /**
     * Constructs a new SOAP binding data with the specified SOAP message and web service context.
     * @param soapMessage the specified SOAP message
     * @param webServiceContext the specified web service context
     */
    public SOAPBindingData(SOAPMessage soapMessage, WebServiceContext webServiceContext) {
        _soapMessage = soapMessage;
        _webServiceContext = webServiceContext;
    }

    /**
     * Gets the SOAP message.
     * @return the SOAP message
     */
    public SOAPMessage getSOAPMessage() {
        return _soapMessage;
    }

    /**
     * Gets the SOAPFault information.
     * @return the SOAPFaultInfo object
     */
    public SOAPFaultInfo getSOAPFaultInfo() {
        return _soapFaultInfo;
    }

    /**
     * Sets the SOAPFault information.
     * @param faultInfo the SOAPFaultInfo object
     */
    public void setSOAPFaultInfo(SOAPFaultInfo faultInfo) {
        _soapFaultInfo = faultInfo;
    }

    /**
     * Gets the web service context.
     * @return the web service context
     */
    public WebServiceContext getWebServiceContext() {
        return _webServiceContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Credential> extractCredentials() {
        Set<Credential> credentials = new HashSet<Credential>();
        credentials.addAll(new SOAPMessageCredentialExtractor().extract(getSOAPMessage()));
        credentials.addAll(new WebServiceContextCredentialExtractor().extract(getWebServiceContext()));
        credentials.addAll(new ServletRequestCredentialExtractor().extract(getServletRequest()));
        return credentials;
    }

    private ServletRequest getServletRequest() {
        if (_webServiceContext != null) {
            return (ServletRequest)_webServiceContext.getMessageContext().get(MessageContext.SERVLET_REQUEST);
        }
        return null;
    }

}
