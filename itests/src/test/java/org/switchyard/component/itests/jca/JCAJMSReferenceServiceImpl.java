package org.switchyard.component.itests.jca;

import javax.inject.Inject;

import org.switchyard.Context;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;

@Service(JCAJMSReferenceService.class)
public class JCAJMSReferenceServiceImpl implements JCAJMSReferenceService {
    
    @Inject @Reference
    private JCAJMSReference service;
    
    @Inject
    private Context context;
    
    @Override
    public void onMessage(String name) {
        service.onMessage(name);
    }
}
