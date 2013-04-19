package org.switchyard.component.jca.deploy;

public interface JCAJMSService {
    public void onMessage(String body) throws JCAJMSFault;
}
