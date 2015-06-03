package org.switchyard.test.jca;

import java.io.InputStream;

public interface JCACCIStreamReference {
    public InputStream onMessage(InputStream body);
}