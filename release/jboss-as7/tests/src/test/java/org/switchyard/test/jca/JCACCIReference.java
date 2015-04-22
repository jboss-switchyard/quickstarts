package org.switchyard.test.jca;

import java.util.Map;

public interface JCACCIReference {
    public Map<String,String> onMessage(Map<String,String> body);
}
