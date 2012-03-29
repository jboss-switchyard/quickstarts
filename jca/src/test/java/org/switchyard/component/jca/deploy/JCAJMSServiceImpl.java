package org.switchyard.component.jca.deploy;

import org.switchyard.component.bean.Service;

@Service(JCAJMSService.class)
public class JCAJMSServiceImpl implements JCAJMSService {
    @Override
    public void onMessage(String name) {
        System.out.println("Hello, " + name + "!!");
    }
}
