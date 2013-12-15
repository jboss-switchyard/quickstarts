package org.switchyard.test.jca;

public interface JCAJMSTransactionService {
    public void onMessage(String body);
    
    public void onMessageText(String body);
    
    public void onMessageManaged(String body);
    
    public void onMessageCamel(String body);
}
