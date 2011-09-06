/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009-11, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.switchyard.component.bpel;

import org.switchyard.HandlerException;
import org.switchyard.annotations.DefaultType;

/**
 * This class represents a thin wrapper around a SOAP Fault
 * generated when a BPEL process returns a fault response.
 *
 */
@DefaultType(BPELFault.MESSAGE_TYPE)
public class BPELFault extends HandlerException {

    /**
     * The message type for the BPEL fault.
     */
    public static final String MESSAGE_TYPE =
            "java:org.switchyard.component.bpel.BPELFault";

    /**
     * Serialization id.
     */
    private static final long serialVersionUID = 1L;

    private javax.xml.soap.SOAPFault _fault=null;

    /**
     * This is the constructor for the BPEL fault.
     *
     * @param fault The SOAP fault being wrapped
     */
    public BPELFault(javax.xml.soap.SOAPFault fault) {
        super("BPEL Fault ["+ fault.getFaultCode()+ "]");
        _fault = fault;
    }

    /**
     * This method returns the SOAP fault.
     *
     * @return The SOAP fault
     */
    public javax.xml.soap.SOAPFault getSOAPFault() {
        return (_fault);
    }
}
