package org.switchyard.component.jca.deploy;

public interface JCAJMSReference {
    public void onMessage(String body);
    public void onMessageText(String body);
}
