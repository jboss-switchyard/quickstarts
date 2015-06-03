package org.switchyard.quickstarts.camel.saxon;

import org.switchyard.component.bean.Service;

@Service(GoodbyeService.class)
public class GoodbyeServiceBean implements GoodbyeService {

    @Override
    public void greet(String name) {
        System.out.println("Goodbye " + name);
    }

}
