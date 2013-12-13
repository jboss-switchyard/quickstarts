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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import org.jboss.logging.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangeState;
import org.switchyard.Message;
import org.switchyard.component.common.composer.BaseMessageComposer;
import org.switchyard.component.soap.SOAPMessages;
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

    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String CONTENT_ID = "Content-ID";
    private static final String CONTENT_ID_START = "<";
    private static final String CONTENT_DISPOSITION_NAME = "name=";
    private static final String CONTENT_DISPOSITION_QUOTE = "\"";
    private static final String TEMP_FILE_EXTENSION = ".tmp";

    private static Logger _log = Logger.getLogger(SOAPMessageComposer.class);
    private Port _wsdlPort;
    private Boolean _documentStyle = false;
    private Boolean _mtomEnabled = false;
    private Boolean _xopExpand = false;
    private Boolean _unwrapped = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public Message compose(SOAPBindingData source, Exchange exchange) throws Exception {
        final SOAPMessage soapMessage = source.getSOAPMessage();
        final Message message = exchange.createMessage();
        getContextMapper().mapFrom(source, exchange.getContext(message));

        final SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        if (envelope == null) {
            return message;
        }

        final SOAPBody soapBody = envelope.getBody();
        if (soapBody == null) {
            throw SOAPMessages.MESSAGES.missingSOAPBodyFromRequest();
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
                        message.setContent(detailNode);
                        return message;
                    }
                }
            }

            List<Element> bodyChildren = getChildElements(soapBody);
            if (bodyChildren.size() > 1) {
                throw SOAPMessages.MESSAGES.foundMultipleSOAPElementsInSOAPBody();
            } else if (bodyChildren.size() == 0 || bodyChildren.get(0) == null) {
                throw SOAPMessages.MESSAGES.couldNotFindSOAPElementInSOAPBody();
            }

            Node bodyNode = bodyChildren.get(0);
            if (_documentStyle) {
                if (_unwrapped) {
                    String opName = exchange.getContract().getConsumerOperation().getName();
                    // peel off the operation wrapper, if present
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
            }
            bodyNode = bodyNode.getParentNode().removeChild(bodyNode);

            // SOAP Attachments
            Map<String, DataSource> attachments = new HashMap<String, DataSource>();
            Iterator<AttachmentPart> aparts = (Iterator<AttachmentPart>) soapMessage.getAttachments();
            while (aparts.hasNext()) {
                AttachmentPart apRequest = aparts.next();
                String[] contentId = apRequest.getMimeHeader(CONTENT_ID);
                String name = null;
                if (_mtomEnabled) {
                    if (contentId == null) {
                        throw SOAPMessages.MESSAGES.contentIDHeaderMissingForAttachmentPart();
                    }
                    name = contentId[0];
                } else {
                    name = apRequest.getDataHandler().getDataSource().getName();
                    if ((name == null) || (name.length() == 0)) {
                        String[] disposition = apRequest.getMimeHeader(CONTENT_DISPOSITION);
                        name = (contentId != null) ? contentId[0] : null;
                        if ((name == null) && (disposition != null)) {
                            int start = disposition[0].indexOf(CONTENT_DISPOSITION_NAME);
                            String namePart = disposition[0].substring(start + CONTENT_DISPOSITION_NAME.length() + 1);
                            int end = namePart.indexOf(CONTENT_DISPOSITION_QUOTE);
                            name = namePart.substring(0, end);
                        } else if (name == null) {
                            // TODO: Identify the extension using content-type
                            name = UUID.randomUUID() + TEMP_FILE_EXTENSION;
                        }
                    }
                }
                if (name.startsWith(CONTENT_ID_START)) {
                    name = name.substring(1, name.length() - 1);
                }
                if (_mtomEnabled && _xopExpand) {
                    // Using a different map because Camel throws java.lang.StackOverflowError
                    // when we do message.removeAttachment(cid);
                    attachments.put(name, apRequest.getDataHandler().getDataSource());
                } else {
                    message.addAttachment(name, apRequest.getDataHandler().getDataSource());
                }
            }
            if (_mtomEnabled && _xopExpand) {
                // Expand xop message by inlining Base64 content
                bodyNode = SOAPUtil.expandXop((Element)bodyNode, attachments);
            }
            message.setContent(bodyNode);
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
        final Boolean input = exchange.getPhase() == null;

        if (message != null) {
            // check to see if the payload is null or it's a full SOAP Message
            if (message.getContent() == null) {
                throw SOAPMessages.MESSAGES.unableToCreateSOAPBodyDueToNullMessageContent();
            }
            if (message.getContent() instanceof SOAPMessage) {
                return new SOAPBindingData((SOAPMessage)message.getContent());
            }
            
            try {
                // convert the message content to a form we can work with
                Node messageNode = message.getContent(Node.class);
                if (messageNode != null) {
                    Node messageNodeImport = soapMessage.getSOAPBody().getOwnerDocument().importNode(messageNode, true);
                    if (exchange.getState() != ExchangeState.FAULT || isSOAPFaultPayload(messageNode)) {
                        if (_documentStyle) {
                            String opName = exchange.getContract().getProviderOperation().getName();
                            if (_unwrapped) {
                                String ns = getWrapperNamespace(opName, input);
                                // Don't wrap if it's already wrapped
                                if (!messageNodeImport.getLocalName().equals(opName + DOC_LIT_WRAPPED_REPLY_SUFFIX)) {
                                    Element wrapper = messageNodeImport.getOwnerDocument().createElementNS(
                                            ns, opName + DOC_LIT_WRAPPED_REPLY_SUFFIX);
                                    wrapper.appendChild(messageNodeImport);
                                    messageNodeImport = wrapper;
                                }
                            }
                        }
                        soapMessage.getSOAPBody().appendChild(messageNodeImport);
                        // SOAP Attachments
                        for (String name : message.getAttachmentMap().keySet()) {
                            AttachmentPart apResponse = soapMessage.createAttachmentPart();
                            apResponse.setDataHandler(new DataHandler(message.getAttachment(name)));
                            apResponse.setContentId("<" + name + ">");
                            soapMessage.addAttachmentPart(apResponse);
                        }
                    } else {
                        // convert to SOAP Fault since ExchangeState is FAULT but the message is not SOAP Fault
                        SOAPUtil.addFault(soapMessage).addDetail().appendChild(messageNodeImport);
                    }
                }
            } catch (Exception e) {
                // Account for exception as payload in case of fault
                if (exchange.getState().equals(ExchangeState.FAULT)
                        && exchange.getMessage().getContent() instanceof Exception) {
                    // Throw the Exception and let JAX-WS format the fault.
                    throw exchange.getMessage().getContent(Exception.class);
                }
                throw SOAPMessages.MESSAGES.unableToParseSOAPMessage(e);
            }
        }
        
        try {
            getContextMapper().mapTo(exchange.getContext(), target);
        } catch (Exception ex) {
            throw SOAPMessages.MESSAGES.failedToMapContextPropertiesToSOAPMessage(ex);
        }

        return target;
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
            Operation operation = WSDLUtil.getOperationByName(_wsdlPort, operationName);
            if (!_documentStyle) {
                ns = input ? operation.getInput().getMessage().getQName().getNamespaceURI()
                    : operation.getOutput().getMessage().getQName().getNamespaceURI();
            } else {
                // Note: WS-I Profile allows only one child under SOAPBody.
                Part part = input ? (Part)operation.getInput().getMessage().getParts().values().iterator().next()
                    : (Part)operation.getOutput().getMessage().getParts().values().iterator().next();
                if (part.getElementName() != null) {
                    ns = part.getElementName().getNamespaceURI();
                } else if (part.getTypeName() != null) {
                    ns = part.getTypeName().getNamespaceURI();
                }
            }
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

    /**
     * Check if the WSDL used is of 'document' style.
     * @return true if 'document' style, false otherwise
     */
    public Boolean isDocumentStyle() {
        return _documentStyle;
    }

    /**
     * Set that the WSDL used is of 'document' style.
     * @param style true or false
     */
    public void setDocumentStyle(Boolean style) {
        _documentStyle = style;
    }

    /**
     * Check if MTOM is enabled.
     * @return true if enabled, false otherwise
     */
    public Boolean isMtomEnabled() {
        return _mtomEnabled;
    }

    /**
     * Set MTOM enabled/disabled.
     * @param enabled true or false
     */
    public void setMtomEnabled(Boolean enabled) {
        _mtomEnabled = enabled;
    }

    /**
     * Check if XOP message should expanded.
     * @return true if expandable, false otherwise
     */
    public Boolean isXopExpand() {
        return _xopExpand;
    }

    /**
     * Set XOP expansion.
     * @param expand true or false
     */
    public void setXopExpand(Boolean expand) {
        _xopExpand = expand;
    }

    /**
     * Check if composer has set unwrap.
     * @return true if expandable, false otherwise
     */
    public Boolean isUnwrapped() {
        return _unwrapped;
    }

    /**
     * Set unwrap flag.
     * @param unwrapped true or false
     */
    public void setUnwrapped(Boolean unwrapped) {
        _unwrapped = unwrapped;
    }

}
