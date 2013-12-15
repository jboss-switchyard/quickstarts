package org.switchyard.test.jca;

public interface JCAJMSService {
    public void onMessage(String body);

    public void onMessage_fault_rollback(String name);

    public void onMessage_fault_commit(String name) throws JCAJMSFault;
}
