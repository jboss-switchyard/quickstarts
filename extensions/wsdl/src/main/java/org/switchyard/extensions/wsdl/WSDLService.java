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

package org.switchyard.extensions.wsdl;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.switchyard.metadata.BaseService;
import org.switchyard.metadata.ServiceOperation;

/**
 * An implementation of ServiceInterface for WSDL files.  The 
 * <code>fromWSDL()</code> method can be used to create a ServiceInterface
 * representation from a WSDL file.  A ServiceOperation will be 
 * created for each operation declared on the WSDL portType.
 * 
 * Operation names are mapped directly from the operation QName.
 * 
 * Message names are created using the input and output declarations of that operation.
 *  
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public final class WSDLService extends BaseService {

    /**
     *  The type returned from ServiceInterface.getType().
     */
    public static final String TYPE = "wsdl";

    private static final String PORTTYPE = "wsdl.porttype";
    
    // WSDL location used to create this ServiceInterface
    private String _wsdlLocation;
    
    // The port type for the ServiceInterface
    private QName _portType;

    /**
     * Private constructor for creating a new ServiceInterface.  Clients of the API
     * should use fromWSDL() to create a ServiceInterface from a WSDL.
     * 
     * @param operations list of operations on the service interface
     * @param wsdlLocation the WSDL location used to derive the interface
     */
    private WSDLService(QName portType, final Set<ServiceOperation> operations, final String wsdlLocation) {
        super(operations, TYPE);
        _portType = portType;
        _wsdlLocation = wsdlLocation;
    }

    /**
     * Creates a ServiceInterface from the specified WSDL. The first found port definition will be used.
     * 
     * @param wsdlLocationURI the WSDL location with port name used to derive the interface
     * @return ServiceInterface representing the WSDL
     * @throws WSDLReaderException if the wsdl cannot be read or is improper
     */
    public static WSDLService fromWSDL(final String wsdlLocationURI) throws WSDLReaderException {
        int index = wsdlLocationURI.indexOf("#");
        if (index > 0) {
            String wsdlLocation = wsdlLocationURI.substring(0, index);
            String localPart = wsdlLocationURI.substring(index + 1);
            String portName = null;
            if (localPart.contains(PORTTYPE)) {
                portName = localPart.substring(PORTTYPE.length() + 1, localPart.length() - 1);
            } else {
                throw new WSDLReaderException("Invalid WSDL interface part " + wsdlLocationURI);
            }
            return fromWSDL(wsdlLocation, portName);
        } else {
            throw new WSDLReaderException("Invalid WSDL interface " + wsdlLocationURI);
        }
    }

    /**
     * Creates a ServiceInterface from the specified WSDL. The first matching port definition
     * with portName will be used.
     * 
     * @param wsdlLocation the WSDL location used to derive the interface 
     * @param portName the port name to match with
     * @return ServiceInterface representing the WSDL
     * @throws WSDLReaderException if the wsdl cannot be read or is improper
     */
    public static WSDLService fromWSDL(final String wsdlLocation, final String portName) throws WSDLReaderException {
        WSDLReader reader = new WSDLReader();
        HashSet<ServiceOperation> ops = reader.readWSDL(wsdlLocation, portName);
        org.w3c.dom.Element wsdl = reader.readWSDL(wsdlLocation);
        
        QName portType = new QName(wsdl.getAttribute("targetNamespace"), portName);
        
        return new WSDLService(portType, ops, wsdlLocation);
    }

    /**
     * Returns the WSDL location used to create this ServiceInterface.
     *
     * @return WSDL definition location
     */
    public String getWSDLLocation() {
        return _wsdlLocation;
    }
    
    /**
     * Returns the Port Type associated with this ServiceInterface.
     * 
     * @return Port type
     */
    public QName getPortType() {
        return _portType;
    }
}
