package org.switchyard.component.bean.tests;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface ConsumerService {
    void consumeInOnlyService(Object message);

    Object consumeInOutService(Object message) throws ConsumerException;
}
