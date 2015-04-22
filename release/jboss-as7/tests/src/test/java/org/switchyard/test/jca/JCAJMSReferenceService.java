package org.switchyard.test.jca;

public interface JCAJMSReferenceService {
    public void onMessage(String body);

    public void onMessageText(String body);

    public void onMessageContextProperty(String body) throws Exception;

    public void onMessagePhysicalName(String body);
}
