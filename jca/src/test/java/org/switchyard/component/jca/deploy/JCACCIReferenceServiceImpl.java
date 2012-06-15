package org.switchyard.component.jca.deploy;

import java.util.Map;

import javax.inject.Inject;

import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;

@Service(JCACCIReferenceService.class)
public class JCACCIReferenceServiceImpl implements JCACCIReferenceService {
    @Inject @Reference
    private JCACCIReference service;

    @Override
    public Map<String,String> onMessage(Map<String,String> map) {
        return service.onMessage(map);
    }
}
