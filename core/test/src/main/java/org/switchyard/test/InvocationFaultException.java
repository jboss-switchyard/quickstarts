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

package org.switchyard.test;

import org.switchyard.Message;

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
