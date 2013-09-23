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
package org.switchyard.component.bpel.riftsaw;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.switchyard.SwitchYardException;
import org.switchyard.component.bpel.BPELMessages;
import org.w3c.dom.Element;

/**
 * WSDL Helper.
 *
 */
public final class WSDLHelper {

    private static final Logger LOG = Logger.getLogger(WSDLHelper.class);
    
    private static final String WSDL_PORTTYPE_PREFIX = "#wsdl.porttype(";

    private WSDLHelper() {
    }
    
    /**
     * This method returns the WSDL definition defined by the location.
     * 
     * @param location the location
     * @return The WSDL definition or null if not found
     * @throws SwitchYardException Failed to locate WSDL
     */
    public static javax.wsdl.Definition getWSDLDefinition(String location) throws SwitchYardException {
        javax.wsdl.Definition ret=null;
        
        if (location == null) {
            throw BPELMessages.MESSAGES.wSDLLocationHasNotBeenSpecified();
        } else {
            try {
                int index=location.indexOf('#');
                
                if (index != -1) {
                    location = location.substring(0, index);
                }
                
                java.net.URL url=Thread.currentThread().getContextClassLoader().getResource(location);
                
                ret = javax.wsdl.factory.WSDLFactory.newInstance().newWSDLReader().readWSDL(url.toString());
                
            } catch (Exception e) {
                throw BPELMessages.MESSAGES.failedToLoadWSDL(location, e);
            }
        }

        return (ret);
    }

    /**
     * This method returns the port type associated with the supplied wsdl and location.
     * 
     * @param location The location
     * @param wsdl The wsdl
     * @return The port type
     * @throws SwitchYardException Failed to get port type
     */
    public static javax.wsdl.PortType getPortType(String location, javax.wsdl.Definition wsdl)
                                    throws SwitchYardException {
        javax.wsdl.PortType ret=null;
        
        if (location == null) {
            throw BPELMessages.MESSAGES.wSDLLocationHasNotBeenSpecified();
        } else {
            int index=location.indexOf(WSDL_PORTTYPE_PREFIX);
            
            if (index != -1) {
                String portTypeName = location.substring(index+WSDL_PORTTYPE_PREFIX.length(), location.length() - 1);
                
                ret = wsdl.getPortType(new QName(wsdl.getTargetNamespace(), portTypeName));
            }
        }

        return (ret);
    }
    
    /**
     * This method returns the service associated with the supplied port type from the
     * supplied WSDL.
     * 
     * @param portType The port type
     * @param wsdl The wsdl
     * @return The service, or null if not found
     */
    public static javax.wsdl.Service getServiceForPortType(javax.wsdl.PortType portType,
                                javax.wsdl.Definition wsdl) {
        javax.wsdl.Service ret=null;
        
        java.util.Iterator<?> iter=wsdl.getServices().values().iterator();
        while (ret == null && iter.hasNext()) {
            ret = (javax.wsdl.Service)iter.next();
            
            java.util.Iterator<?> ports=ret.getPorts().values().iterator();
            boolean f_found=false;
            
            while (!f_found && ports.hasNext()) {
                javax.wsdl.Port port=(javax.wsdl.Port)ports.next(); 
                
                if (port.getBinding().getPortType() == portType) {
                    f_found = true;
                }
            }
            
            if (!f_found) {
                ret = null;
            }
        }
        
        return (ret);
    }

    /**
     * This method removes the message content from its part wrapper.
     * 
     * @param content The wrapped part
     * @return The unwrapped content
     */
    public static org.w3c.dom.Element unwrapMessagePart(org.w3c.dom.Element content) {
        
        org.w3c.dom.NodeList nl=content.getChildNodes();
        
        for (int i=0; i < nl.getLength(); i++) {
            
            if (nl.item(i) instanceof org.w3c.dom.Element) {
                
                org.w3c.dom.NodeList nl2=((org.w3c.dom.Element)nl.item(i)).getChildNodes();
                
                for (int j=0; j < nl2.getLength(); j++) {
                    
                    if (nl2.item(j) instanceof org.w3c.dom.Element) {
                        return ((org.w3c.dom.Element)nl2.item(j));
                    }
                }
                
                return (null);
            }
        }
        
        return (null);
    }

    /**
     * This method wraps a request message content in a part wrapper.
     * 
     * @param content The message
     * @param operation The operation
     * @return The part wrapper associated with the operation
     */
    public static org.w3c.dom.Element wrapRequestMessagePart(org.w3c.dom.Element content,
                        javax.wsdl.Operation operation) {
        return (wrapMessagePart(content, operation, operation.getInput().getMessage().getParts(), false));
    }

    /**
     * This method wraps a response message content in a part wrapper.
     * 
     * @param content The message
     * @param operation The operation
     * @return The part wrapper associated with the operation
     */
    public static org.w3c.dom.Element wrapResponseMessagePart(org.w3c.dom.Element content,
                        javax.wsdl.Operation operation) {
        return (wrapMessagePart(content, operation, operation.getOutput().getMessage().getParts(), false));
    }

    /**
     * This method wraps a fault message content in a part wrapper.
     * 
     * @param content The message
     * @param operation The operation
     * @param faultName The fault
     * @return The part wrapper associated with the operation and fault
     */
    public static org.w3c.dom.Element wrapFaultMessagePart(org.w3c.dom.Element content,
                        javax.wsdl.Operation operation, String faultName) {
        java.util.Map<?, ?> parts=null;
        
        if (faultName != null) {
            javax.wsdl.Fault fault=operation.getFault(faultName);
            
            if (fault == null) {
                throw BPELMessages.MESSAGES.unableToFindFaultOn(faultName, operation.getName());
            }
            
            parts = fault.getMessage().getParts();
        } else {
            // Need to iterate through faults to determine which has a part with the
            // appropriate element type
            @SuppressWarnings({ "unchecked" })
            java.util.Iterator<javax.wsdl.Fault> iter=
                    (java.util.Iterator<javax.wsdl.Fault>)operation.getFaults().values().iterator();
            
            while (parts == null && iter.hasNext()) {
                javax.wsdl.Fault fault=iter.next();
                
                if (fault.getMessage().getParts().size() == 1) {
                    javax.wsdl.Part part=(javax.wsdl.Part)fault.getMessage().getParts().values().iterator().next();
                    
                    if (part.getElementName() != null
                            && content.getLocalName().equals(part.getElementName().getLocalPart()) 
                            && content.getNamespaceURI().equals(part.getElementName().getNamespaceURI())) {
                        
                        parts = fault.getMessage().getParts();
                    }
                }
            }
        }
        
        return (wrapMessagePart(content, operation, parts, true));
    }

    /**
     * This method wraps a message content in a part wrapper.
     * 
     * @param content The message
     * @param operation The operation
     * @param parts The parts map
     * @param fault Whether dealing with a fault
     * @return The part wrapper associated with the operation
     */
    protected static org.w3c.dom.Element wrapMessagePart(org.w3c.dom.Element content,
                    javax.wsdl.Operation operation, java.util.Map<?, ?> parts, boolean fault) {
        org.w3c.dom.Element ret=content.getOwnerDocument().createElement("message");
        String partName=null;
        
        // Find part name from content type for the operation
        if (parts != null) {
            if (parts.size() != 1) {
                throw BPELMessages.MESSAGES.onlyExpectingASingleMessagePartForOperation(operation.getName());
            }
            
            partName = (String)parts.keySet().iterator().next();
        }
        
        if (partName == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No part found for operation: "+operation+" and content: "+content);
            }
            
            if (!fault) {
                throw BPELMessages.MESSAGES.unableToFindPartNameFor(operation.getName());
            }
            
            // Assume that this represents an undeclared fault, and therefore return as an
            // element instead of a message (RIFTSAW-516)
            return (content);
        }
        
        Element part=ret.getOwnerDocument().createElement(partName);
        ret.appendChild(part);
        part.appendChild(content);        
        
        return (ret);
    }
}
