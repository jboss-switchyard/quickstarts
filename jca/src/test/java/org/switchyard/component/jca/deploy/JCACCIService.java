package org.switchyard.component.jca.deploy;

import java.util.Map;

public interface JCACCIService {
    public Map<String,String> onMessage(Map<String,String> map);
}
