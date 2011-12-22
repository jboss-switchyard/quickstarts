package org.switchyard.quickstarts.transform.json;

import org.switchyard.annotations.OperationTypes;

public interface JsonOrderService {

    @OperationTypes(in="{urn:switchyard-quickstart:transform-json:1.0}order",
            out="{urn:switchyard-quickstart:transform-json:1.0}orderResponse")
    String submitOrder(String orderJson);
    
}
