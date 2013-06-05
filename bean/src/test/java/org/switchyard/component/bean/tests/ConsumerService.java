package org.switchyard.component.bean.tests;

import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface ConsumerService {
    void consumeInOnlyNoArgsService();
    
    void consumeInOnlyService(Object message);

    Object consumeInOutService(Object message) throws ConsumerException;

    Object consumeInOutServiceThrowsRuntimeException(Object message);
    
    String domOperation(Document message);
}
