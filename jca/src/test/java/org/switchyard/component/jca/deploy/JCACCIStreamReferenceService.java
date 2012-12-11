package org.switchyard.component.jca.deploy;

import java.io.InputStream;

public interface JCACCIStreamReferenceService {
    public InputStream onMessage(InputStream body);
}