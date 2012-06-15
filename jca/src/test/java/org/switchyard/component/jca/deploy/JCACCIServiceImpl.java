package org.switchyard.component.jca.deploy;

import java.util.Map;

import org.switchyard.component.bean.Service;

@Service(JCACCIService.class)
public class JCACCIServiceImpl implements JCACCIService {
    @Override
    public Map<String,String> onMessage(Map<String,String> map) {
        map.put("input", "Hello " + map.get("input") + " !");
        return map;
    }
}
