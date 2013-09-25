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
package org.switchyard.component.soap.composer;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.switchyard.component.common.composer.SecurityBindingData;
import org.switchyard.component.soap.SOAPLogger;
import org.switchyard.security.credential.Credential;
import org.switchyard.security.credential.extractor.SOAPMessageCredentialExtractor;
import org.switchyard.security.credential.extractor.ServletRequestCredentialExtractor;
import org.switchyard.security.credential.extractor.WebServiceContextCredentialExtractor;

/**
 * SOAP binding data.
 * 
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 * @author Magesh Kumar B <mageshbk@jboss.com> &copy; 2013 Red Hat Inc.
 */
public class SOAPBindingData implements SecurityBindingData {

    private final SOAPMessage _soapMessage;
    private final WebServiceContext _webServiceContext;
    private SOAPFaultInfo _soapFaultInfo;
    private Integer _status;

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
     * Get the HTTP response status.
     * @return HTTP response status
     */
    public Integer getStatus() {
        return _status;
    }

    /**
     * Set the HTTP response status.
     * @param status the response status
     */
    public void setStatus(Integer status) {
        _status = status;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Credential> extractCredentials() {
        Set<Credential> credentials = new HashSet<Credential>();
        credentials.addAll(new SOAPMessageCredentialExtractor().extract(getSOAPMessage()));
        credentials.addAll(new WebServiceContextCredentialExtractor().extract(getWebServiceContext()));
        try {
            credentials.addAll(new ServletRequestCredentialExtractor().extract(getServletRequest()));
        } catch (UnsupportedOperationException uoe) {
            // Ignore. This can happen with JBossWS http transport
            SOAPLogger.ROOT_LOGGER.credentialsAreIgnoredForServletRequest();
        }
        return credentials;
    }

    private ServletRequest getServletRequest() {
        if (_webServiceContext != null) {
            return (ServletRequest)_webServiceContext.getMessageContext().get(MessageContext.SERVLET_REQUEST);
        }
        return null;
    }

}
