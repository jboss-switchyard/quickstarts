package org.switchyard.quickstarts.camel.amqp.binding;

import org.switchyard.component.bean.Service;

@Service(GreetingService.class)
public class GreetingServiceBean implements GreetingService {

    @Override
    public void greet(String name) {
        System.out.println("Hola " + name + " ;-) ");
    }


}
