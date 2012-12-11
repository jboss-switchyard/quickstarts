package org.switchyard.component.jca.deploy;

import java.io.InputStream;

import javax.inject.Inject;

import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;

@Service(JCACCIStreamReferenceService.class)
public class JCACCIStreamReferenceServiceImpl implements JCACCIStreamReferenceService {
    @Inject @Reference
    private JCACCIStreamReference service;

    @Override
    public InputStream onMessage(InputStream body) {
        return service.onMessage(body);
    }
}
