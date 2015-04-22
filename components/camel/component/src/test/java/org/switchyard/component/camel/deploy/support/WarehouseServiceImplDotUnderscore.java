package org.switchyard.component.camel.deploy.support;

import java.io.IOException;

import javax.inject.Inject;

import org.switchyard.Message;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.component.bean.Service;

@Service(name="Warehouse.Service_1")
public class WarehouseServiceImplDotUnderscore implements WarehouseService {

    @Override
    public String hasItem(Integer id) {
        return "Fletch";
    }

    @Inject
    Message _message;

    @Override
    public String getDataForItem(String itemId) {
        try {
            return new StringPuller().pull(_message.getAttachment(itemId).getInputStream());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

}
