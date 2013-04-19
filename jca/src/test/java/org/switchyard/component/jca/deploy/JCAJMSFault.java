package org.switchyard.component.jca.deploy;

@SuppressWarnings("serial")
public class JCAJMSFault extends Exception {
    public JCAJMSFault(String message) {
        super(message);
    }
}
