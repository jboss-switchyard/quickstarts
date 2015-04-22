package org.switchyard.test.jca;

import java.io.InputStream;

public interface JCACCIStreamReferenceService {
    public InputStream onMessage(InputStream body);
}