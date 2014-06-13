package org.switchyard.quickstarts.camel.saxon;

import org.switchyard.component.bean.Service;

@Service(HelloService.class)
public class HelloServiceBean implements HelloService {

    @Override
    public void greet(String name) {
        System.out.println("Hello " + name);
    }

}
