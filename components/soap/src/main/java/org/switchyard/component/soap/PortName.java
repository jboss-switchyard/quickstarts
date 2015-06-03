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
 
package org.switchyard.component.soap;

import java.io.Serializable;
import javax.xml.namespace.QName;

/**
 * Represents the SOAPGateway's wsPort parameter. The value of a PortName contains a Service QName and Port name.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class PortName implements Serializable {
    private static final long serialVersionUID = -1904811344687200683L;
    
    private QName _serviceQName = new QName("");
    private String _name;

    /**
     * Default Constructor.
     */
    public PortName() {
    }
    
    /**
     * Construct the PortName from a string.
     *
     * The string can be in the form "{namespaceURI}serviceName:portName", with the "{namespaceURI}" and "serviceName:" part being optional.
     * If the PortName need to represent only a service name then the string could be just "serviceName:"
     *
     * @param portName a port name String.
     */
    public PortName(final String portName) {
        if (portName != null) {
            int idx = portName.lastIndexOf(":");
            _name = portName.substring(idx + 1, portName.length());
            if (idx > 0) {
                String serviceStr = portName.substring(0, idx);
                _serviceQName = QName.valueOf(serviceStr);
            }
        }
    }
    
    /**
     * Construct the PortName given a Service QName and Port name.
     *
     * @param serviceQName The Service QName.
     * @param name a port name.
     */
    public PortName(final QName serviceQName, final String name) {
        if (serviceQName != null) {
            _serviceQName = serviceQName;
        }
        _name = name;
    }
    
    /**
     * Construct the PortName given a namespace URI, Service name and Port name.
     *
     * @param namespace a namespace URI.
     * @param serviceName the Service QName.
     * @param name the port name.
     */
    public PortName(final String namespace, final String serviceName, final String name) {
        _serviceQName = new QName(namespace, serviceName);
        _name = name;
    }
    
    /**
     * Get the Port as a QName.
     *
     * @return the Port QName.
     */
    public QName getPortQName() {
        return new QName(getNamespaceURI(), _name);
    }
    
    /**
     * Get the Port's Service QName.
     *
     * @return the Service QName.
     */
    public QName getServiceQName() {
        return _serviceQName;
    }

    /**
     * Set the Port's Service QName.
     *
     * @param serviceQName a Service QName.
     */
    public void setServiceQName(final QName serviceQName) {
        _serviceQName = serviceQName;
    }
    
    /**
     * Get the Port's Service name.
     *
     * @return the Service name.
     */
    public String getServiceName() {
        return _serviceQName.getLocalPart();
    }

    /**
     * Set the Port's Service name.
     *
     * @param serviceName a Service name.
     */
    public void setServiceName(final String serviceName) {
        String namespaceURI = _serviceQName.getNamespaceURI();
        _serviceQName = new QName(namespaceURI, serviceName);
    }
    
    /**
     * Get the Port's Service namespace URI.
     *
     * @return the namespace URI.
     */
    public String getNamespaceURI() {
        return _serviceQName.getNamespaceURI();
    }

    /**
     * Set the Port's Service namespace URI.
     *
     * @param namespaceURI a namespaceURI String.
     */
    public void setNamespaceURI(final String namespaceURI) {
        String serviceName = _serviceQName.getLocalPart();
        _serviceQName = new QName(namespaceURI, serviceName);
    }
    
    /**
     * Get the Port's name.
     *
     * @return the Port name String.
     */
    public String getName() {
        return _name;
    }

    /**
     * Set the Port name.
     *
     * @param name a Port name.
     */
    public void setName(final String name) {
        _name = name;
    }

    /**
     * Test this PortName for equality with another Object.
     *
     * @param objectToTest the Object to test with this PortName.
     * @return true if equal else false.
     */
    public final boolean equals(Object objectToTest) {
        if ((objectToTest == null) || (!(objectToTest instanceof PortName))) {
            return false;
        }

        PortName portName = (PortName) objectToTest;

        return (this._name.equals(portName._name)) && (this._serviceQName.equals(portName._serviceQName));
    }

    /**
     * Generate the hashcode for this PortName.
     *
     * @return The hashcode.
     */
    public final int hashCode() {
        return _serviceQName.hashCode() ^ _name.hashCode();
    }

    /**
     * Retruns a String representation of PortName in the form "{namespaceURI}serviceName:portName".
     * If the serviceQName is null then returns only the portName.
     *
     * @return A PortName string.
     */
    public String toString() {
        if (_serviceQName != null) {
            return _serviceQName + ":" + _name;
        } else {
            return _name;
        }
    }
}
