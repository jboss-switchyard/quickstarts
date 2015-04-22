package org.switchyard.component.bean;

import org.switchyard.Context;
import org.switchyard.Message;

/**
 * An instance of an invocation for an @Reference.  A single instance of 
 * ReferenceInvocation cannot be used for multiple invocations of a service.
 * Message content and context properties for an invocation can be accessed 
 * through this interface.
 */
public interface ReferenceInvocation {

    /**
     * Access the context for a reference.
     * @return context
     */
    Context getContext();
    
    /**
     * Access the current message for a reference.  Before invoke() is called
     * this will return the IN message.  After invoke() is called, this will
     * return the OUT message for IN_OUT MEPs.
     * @return current message.
     */
    Message getMessage();
    
    /**
     * Invoke the target service.
     * @return a reference to this ReferenceInvocation for chaining calls
     * @throws Exception fault encountered during the invocation 
     */
    ReferenceInvocation invoke() throws Exception;
    
    /**
     * Invoke the target service using the specified object as the message content.
     * @param content message content
     * @return a reference to this ReferenceInvocation for chaining calls
     * @throws Exception fault encountered during the invocation 
     */
    ReferenceInvocation invoke(Object content) throws Exception;
    
    /**
     * Convenience method for setting a message-scoped context property for the 
     * invocation.
     * @param name property name
     * @param value property value
     * @return a reference to this ReferenceInvocation for chaining calls
     */
    ReferenceInvocation setProperty(String name, String value);
    
    /**
     * Convenience method to retrieve a context property.
     * @param name property name
     * @return property value
     */
    Object getProperty(String name);
}
