/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.quickstarts.camel.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Response definition.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "_greeting"
})
@XmlRootElement(name = "response")
public class GreetingResponse {

    @XmlElement(name = "greeting", required = true)
    private String _greeting;

    /**
     * Constructs response.
     */
    public GreetingResponse() {
    }

    /**
     * Constructs response.
     * @param value Reply string.
     */
    public GreetingResponse(String value) {
        _greeting = value;
    }

    /**
     * Gets the value of the greeting property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGreeting() {
        return _greeting;
    }

    /**
     * Sets the value of the greeting property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGreeting(String value) {
        this._greeting = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_greeting == null) ? 0 : _greeting.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GreetingResponse other = (GreetingResponse) obj;
        if (_greeting == null) {
            if (other._greeting != null) {
                return false;
            }
        } else if (!_greeting.equals(other._greeting)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Greeting [" + _greeting + "]";
    }

}
