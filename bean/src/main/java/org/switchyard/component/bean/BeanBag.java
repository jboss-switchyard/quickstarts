package org.switchyard.component.bean;

import org.switchyard.Context;
import org.switchyard.Message;

/**
 * BeanBag provides access to the Context object for passed in service reference,
 * as well as request Context and request Message.
 */
public interface BeanBag {
    
    /**
     * get request context.
     * @return request context
     */
    Context getInContext();
    
    /**
     * get request context for reference.
     * @param reference target service reference name
     * @return request context for passed in reference
     */
    Context getInContext(String reference);
    
    /**
     * get request message.
     * @return request message
     */
    Message getOutMessage();
}
