package org.switchyard.component.jca.deploy;

import java.io.InputStream;

public interface JCACCIStreamReference {
    public InputStream onMessage(InputStream body);
}