package org.switchyard.component.bean.omservice.basic;

import org.switchyard.component.bean.omservice.model.OrderRequest;
import org.switchyard.component.bean.omservice.model.OrderResponse;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface BasicOrderManagementService {
    OrderResponse createOrder(OrderRequest request);
}
