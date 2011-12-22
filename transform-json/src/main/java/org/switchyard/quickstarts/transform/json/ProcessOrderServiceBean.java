package org.switchyard.quickstarts.transform.json;

import javax.inject.Inject;

import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;

@Service(ProcessOrderService.class)
public class ProcessOrderServiceBean implements ProcessOrderService {
    @Inject @Reference("OrderService")
    private JsonOrderService orderService;
    @Inject @Reference
    private StoreResponseService storeResponseService;
    
    @Override
    public void processOrder(String orderJson) {
        storeResponseService.processOrder(orderService.submitOrder(orderJson));
    }
}
