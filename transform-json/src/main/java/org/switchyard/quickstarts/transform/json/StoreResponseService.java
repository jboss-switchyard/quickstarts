package org.switchyard.quickstarts.transform.json;

public interface StoreResponseService {

    void processOrder(String orderAckJson);
    
}
