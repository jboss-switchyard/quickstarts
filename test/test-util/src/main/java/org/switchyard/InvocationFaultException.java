/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2010-2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
 * details. You should have received a copy of the GNU Lesser General Public 
 * License, v.2.1 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301, USA.
 */

package org.switchyard;

/**
 * Represents an exchange fault in the form of an exception.  Invoker will
 * throw an InvocationFaultException when a message exchange results in a 
 * fault message. <br><br>
 * NOTE : InvocationFaultException is an unchecked Exception so that test methods
 * that don't care about handling it explicitly (e.g. exception = automatic failure)
 * are not required to code try/catch or declare a throws.
 */
public class InvocationFaultException extends RuntimeException {

    private static final long serialVersionUID = 4261226339096599247L;
    
    private Message _faultMessage;
    
    /**
     * Create a new InvocationFaultException based on the specified fault message.
     * @param faultMessage fault message
     */
    public InvocationFaultException(Message faultMessage) {
        super();
        _faultMessage = faultMessage;
    }
    
    /**
     * Return the underlying fault message.
     * @return fault message
     */
    public Message getFaultMessage() {
        return _faultMessage;
    }
    
    /**
     * Determines if the content of the underlying fault message matches the 
     * specified type.  If the content is an exception, we traverse the parent
     * graph to see if any of the exceptions match the specified type.
     * @param type expected type
     * @return true if the fault content is of the specified type, false otherwise
     */
    public boolean isType(Class<?> type) {
        if (Throwable.class.isAssignableFrom(_faultMessage.getContent().getClass())) {
            Throwable t = _faultMessage.getContent(Throwable.class);
            for (; t != null; t = t.getCause()) {
                if (t.getClass().isAssignableFrom(type)) {
                    return true;
                }
            }
        }
        // not a nested throwable, so see if it matches directly   
        return _faultMessage.getContent().getClass().isAssignableFrom(type);
    }
    
    @Override
    public Throwable getCause() {
        if (Throwable.class.isAssignableFrom(_faultMessage.getContent().getClass())) {
            return _faultMessage.getContent(Throwable.class);
        } else {
            return super.getCause();
        }
    }
}
