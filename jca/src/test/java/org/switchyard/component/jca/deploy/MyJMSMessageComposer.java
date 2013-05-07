package org.switchyard.component.jca.deploy;

import org.switchyard.Exchange;
import org.switchyard.component.jca.composer.JMSBindingData;
import org.switchyard.component.jca.composer.JMSMessageComposer;

public class MyJMSMessageComposer extends JMSMessageComposer {

    @Override
    public org.switchyard.Message compose(JMSBindingData source, Exchange exchange) throws Exception {
        org.switchyard.Message msg = super.compose(source, exchange);
        msg.setContent(msg.getContent(String.class) + "test");
        return msg;
    }
    
    @Override
    public JMSBindingData decompose(Exchange exchange, JMSBindingData target) throws Exception {
        exchange.getMessage().setContent(exchange.getMessage().getContent(String.class)+"test");
        return super.decompose(exchange, target);
    }
    
}
