package org.switchyard.test.jca;

import java.util.Map;

public interface JCACCIReferenceService {
    public Map<String,String> onMessage(Map<String,String> map);
}
