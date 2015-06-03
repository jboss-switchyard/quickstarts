package org.switchyard.test.jca;

@SuppressWarnings("serial")
public class JCAJMSFault extends Exception {
    public JCAJMSFault(String message) {
        super(message);
    }
}
