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

package org.switchyard.component.soap.composer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.dom.DOMSource;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangeState;
import org.switchyard.Message;
import org.switchyard.component.common.composer.BaseMessageComposer;
import org.switchyard.component.soap.config.model.SOAPMessageComposerModel;
import org.switchyard.component.soap.util.SOAPUtil;
import org.switchyard.component.soap.util.WSDLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The SOAP implementation of MessageComposer simply copies the SOAP body into
 * the Message and SOAP headers into the Message's context, and vice-versa.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SOAPMessageComposer extends BaseMessageComposer<SOAPBindingData> {

    // Constant suffix used for the reply wrapper when the composer is configured to 
    // wrap response messages with operation name.
    private static final String DOC_LIT_WRAPPED_REPLY_SUFFIX = "Response";
    
    private static Logger _log = Logger.getLogger(SOAPMessageComposer.class);
    private SOAPMessageComposerModel _config;
    private Port _wsdlPort;

    /**
     * {@inheritDoc}
     */
    @Override
    public Message compose(SOAPBindingData source, Exchange exchange, boolean create) throws Exception {
        final SOAPMessage soapMessage = source.getSOAPMessage();
        final Message message = create ? exchange.createMessage() : exchange.getMessage();

        getContextMapper().mapFrom(source, exchange.getContext(message));

        final SOAPBody soapBody = soapMessage.getSOAPBody();
        if (soapBody == null) {
            throw new SOAPException("Missing SOAP body from request");
        }

        try {
            if (soapBody.hasFault()) {
                // peel off the Fault element
                SOAPFault fault = soapBody.getFault();
                if (fault.hasDetail()) {
                    Detail detail = fault.getDetail();
                    // We only support one entry at this moment
                    DetailEntry entry = null;
                    Iterator<DetailEntry> entries = detail.getDetailEntries();
                    if (entries.hasNext()) {
                        entry = entries.next();
                    }
                    if (entry != null) {
                        Node detailNode = entry.getParentNode().removeChild(entry);
                        message.setContent(new DOMSource(detailNode));
                        return message;
                    }
                }
            }

            List<Element> bodyChildren = getChildElements(soapBody);
            if (bodyChildren.size() > 1) {
                throw new SOAPException("Found multiple SOAPElements in SOAPBody");
            } else if (bodyChildren.size() == 0 || bodyChildren.get(0) == null) {
                throw new SOAPException("Could not find SOAPElement in SOAPBody");
            }

            Node bodyNode = bodyChildren.get(0);
            if (_config != null && _config.isUnwrapped()) {
                // peel off the operation wrapper, if present
                String opName = exchange.getContract().getConsumerOperation().getName();
                if (opName != null && opName.equals(bodyNode.getLocalName())) {
                    List<Element> subChildren = getChildElements(bodyNode);
                    if (subChildren.size() == 0 || subChildren.size() > 1) {
                        _log.debug("Unable to unwrap element: " + bodyNode.getLocalName()
                               + ". A single child element is required.");
                    } else {
                        bodyNode = subChildren.get(0);
                    }
                }
            }
            bodyNode = bodyNode.getParentNode().removeChild(bodyNode);
            message.setContent(new DOMSource(bodyNode));
        } catch (Exception ex) {
            if (ex instanceof SOAPException) {
                throw (SOAPException) ex;
            }
            throw new SOAPException(ex);
        }

        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPBindingData decompose(Exchange exchange, SOAPBindingData target) throws Exception {
        final SOAPMessage soapMessage = target.getSOAPMessage();
        final Message message = exchange.getMessage();

        if (message != null) {
            // check to see if the payload is null or it's a full SOAP Message
            if (message.getContent() == null) {
                throw new SOAPException("Unable to create SOAP Body due to null message content");
            }
            if (message.getContent() instanceof SOAPMessage) {
                return new SOAPBindingData((SOAPMessage)message.getContent());
            }
            
            try {
                // convert the message content to a form we can work with
                org.w3c.dom.Node input = message.getContent(org.w3c.dom.Node.class);
                org.w3c.dom.Node messageNodeImport = soapMessage.getSOAPBody().getOwnerDocument().importNode(input, true);
                if (exchange.getState() != ExchangeState.FAULT || isSOAPFaultPayload(input)) {
                    if (_config != null && _config.isUnwrapped()) {
                        String opName = exchange.getContract().getProviderOperation().getName();
                        String ns = getWrapperNamespace(opName, exchange.getPhase() == null);
                        // Don't wrap if it's already wrapped
                        if (!messageNodeImport.getLocalName().equals(opName + DOC_LIT_WRAPPED_REPLY_SUFFIX)) {
                            Element wrapper = messageNodeImport.getOwnerDocument().createElementNS(
                                    ns, opName + DOC_LIT_WRAPPED_REPLY_SUFFIX);
                            wrapper.appendChild(messageNodeImport);
                            messageNodeImport = wrapper;
                        }
                    }
                    soapMessage.getSOAPBody().appendChild(messageNodeImport);
                } else {
                    // convert to SOAP Fault since ExchangeState is FAULT but the message is not SOAP Fault
                    SOAPUtil.addFault(soapMessage).addDetail().appendChild(messageNodeImport);
                }
            } catch (Exception e) {
                // Account for exception as payload in case of fault
                if (exchange.getState().equals(ExchangeState.FAULT)
                        && exchange.getMessage().getContent() instanceof Exception) {
                    // Throw the Exception and let JAX-WS format the fault.
                    throw exchange.getMessage().getContent(Exception.class);
                }
                throw new SOAPException("Unable to parse SOAP Message", e);
            }
        }
        
        try {
            getContextMapper().mapTo(exchange.getContext(), target);
        } catch (Exception ex) {
            throw new SOAPException("Failed to map context properties to SOAP message", ex);
        }

        return target;
    }
    
    /**
     * Gets the SOAPMessageComposerModel config.
     * @return the SOAPMessageComposerModel
     */
    public SOAPMessageComposerModel getComposerConfig() {
        return _config;
    }

    /**
     * Sets the SOAPMessageComposerModel config.
     * @param composerConfig configuration
     */
    public void setComposerConfig(SOAPMessageComposerModel composerConfig) {
        _config = composerConfig;
    }

    private boolean isSOAPFaultPayload(org.w3c.dom.Node messageNode) {
        String rootName = messageNode.getLocalName().toLowerCase();

        if (rootName.equals("fault")) {
            String nsURI = messageNode.getNamespaceURI();

            if (nsURI.equals(SOAPUtil.SOAP12_URI) || nsURI.equals(SOAPUtil.SOAP11_URI)) {
                return true;
            }
        }

        return false;
    }
    
    // Retrieves the immediate child of the specified parent element
    private List<Element> getChildElements(Node parent) {
        List<Element> children = new ArrayList<Element>();
        NodeList nodes = parent.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                children.add((Element)nodes.item(i));
            }
        }
        
        return children;
    }
    
    private String getWrapperNamespace(String operationName, boolean input) {
        String ns = null;
        
        if (_wsdlPort != null) {
            Operation op = WSDLUtil.getOperationByName(_wsdlPort, operationName);
            @SuppressWarnings("unchecked")
            List<Part> parts = input ? op.getInput().getMessage().getOrderedParts(null) 
                    : op.getOutput().getMessage().getOrderedParts(null);
            
            ns = parts.get(0).getElementName().getNamespaceURI();
        }
        
        return ns;
    }

    /**
     * Get the WSDL Port used by this message composer.
     * @return the wsdlPort
     */
    public Port getWsdlPort() {
        return _wsdlPort;
    }

    /**
     * Set the WSDL Port used by this message composer.
     * @param wsdlPort WSDL port
     */
    public void setWsdlPort(Port wsdlPort) {
        _wsdlPort = wsdlPort;
    }

}
