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
package org.switchyard.component.bpel;

import org.switchyard.HandlerException;
import org.switchyard.annotations.DefaultType;

/**
 * This class represents a thin wrapper around a SOAP Fault
 * generated when a BPEL process returns a fault response.
 *
 */
@Deprecated
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
