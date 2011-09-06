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
package org.switchyard.component.bpel.riftsaw;

import javax.xml.namespace.QName;

import org.switchyard.exception.SwitchYardException;
import org.w3c.dom.Element;

/**
 * WSDL Helper.
 *
 */
public final class WSDLHelper {

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
            throw new SwitchYardException("WSDL location has not been specified");
        } else {
            try {
                int index=location.indexOf('#');
                
                if (index != -1) {
                    location = location.substring(0, index);
                }
                
                java.net.URL url=Thread.currentThread().getContextClassLoader().getResource(location);
                
                ret = javax.wsdl.factory.WSDLFactory.newInstance().newWSDLReader().readWSDL(url.toString());
                
            } catch (Exception e) {
                throw new SwitchYardException("Failed to load WSDL '"+location+"'", e);
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
            throw new SwitchYardException("WSDL location has not been specified");
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
        return ((org.w3c.dom.Element)content.getFirstChild().getFirstChild());
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
        return (wrapMessagePart(content, operation, operation.getInput().getMessage().getParts()));
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
        return (wrapMessagePart(content, operation, operation.getOutput().getMessage().getParts()));
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
                throw new SwitchYardException("Unable to find fault '"+faultName+"' on "
                        +"operation '"+operation.getName()+"'");
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
        
        return (wrapMessagePart(content, operation, parts));
    }

    /**
     * This method wraps a message content in a part wrapper.
     * 
     * @param content The message
     * @param operation The operation
     * @param parts The parts map
     * @return The part wrapper associated with the operation
     */
    protected static org.w3c.dom.Element wrapMessagePart(org.w3c.dom.Element content,
                    javax.wsdl.Operation operation, java.util.Map<?, ?> parts) {
        org.w3c.dom.Element ret=content.getOwnerDocument().createElement("message");
        String partName=null;
        
        // Find part name from content type for the operation
        if (parts != null) {
            if (parts.size() != 1) {
                throw new SwitchYardException("Only expecting a single message part for operation '"
                        +operation.getName()+"'");
            }
            
            partName = (String)parts.keySet().iterator().next();
        }
        
        if (partName == null) {
            throw new SwitchYardException("Unable to find part name for "
                        +"operation '"+operation.getName()+"'");
        }
        
        Element part=ret.getOwnerDocument().createElement(partName);
        ret.appendChild(part);
        part.appendChild(content);        
        
        return (ret);
    }
}
