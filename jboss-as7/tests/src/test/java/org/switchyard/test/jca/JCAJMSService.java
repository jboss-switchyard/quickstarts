package org.switchyard.test.jca;

public interface JCAJMSService {
    public void onMessage(String body);

    public void onMessage_fault_rollback(String name);

    public void onMessage_fault_commit(String name) throws JCAJMSFault;

    public String onMessage_inout(String name);

    public String onMessage_inout_fault(String name) throws JCAJMSFault;

    public String onMessage_inout_context_property(String name);

    public String onMessage_inout_physical_name(String name);

    public String onMessage_inout_physical_name_fault(String name) throws JCAJMSFault;
}
