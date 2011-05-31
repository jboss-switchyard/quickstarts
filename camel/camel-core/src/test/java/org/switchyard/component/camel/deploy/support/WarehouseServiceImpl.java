package org.switchyard.component.camel.deploy.support;

import org.switchyard.component.bean.Service;

@Service(WarehouseService.class)
public class WarehouseServiceImpl implements WarehouseService
{
    
    @Override
    public String hasItem(String id)
    {
        return "Fletch";
    }

}
