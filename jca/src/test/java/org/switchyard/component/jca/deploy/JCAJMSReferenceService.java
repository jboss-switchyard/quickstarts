package org.switchyard.component.jca.deploy;

public interface JCAJMSReferenceService {
    public void onMessage(String body);
    
    public void onMessageText(String body);
}
