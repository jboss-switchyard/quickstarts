package org.switchyard.component.jca.deploy;

import javax.inject.Inject;

import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;

@Service(JCAJMSReferenceService.class)
public class JCAJMSReferenceServiceImpl implements JCAJMSReferenceService {
    @Inject @Reference
    private JCAJMSReference service;
    
    @Inject @Reference("JCAJMSReferenceText")
    private JCAJMSReference serviceText;

    @Override
    public void onMessage(String name) {
        service.onMessage(name);
    }
    
    @Override
    public void onMessageText(String name) {
        serviceText.onMessageText(name);
    }
}
