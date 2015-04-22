package org.switchyard.component.bean.exchange;

import javax.activation.DataSource;
import javax.inject.Inject;

import org.junit.Assert;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.component.bean.Service;

@Service(value = ExchangeInjection.class, name = "ExchangeInjectionService")
public class ExchangeInjectionBean implements ExchangeInjection {
    
    @Inject
    private Exchange exchange;

    @Override
    public String injectionNotNull(String msg) {
        Assert.assertNotNull(exchange);
        return msg;
    }

    @Override
    public String correctMessage(String msg) {
        Assert.assertEquals(ExchangeInjectionTest.TEST_IN_CONTENT, msg);
        Assert.assertEquals(ExchangeInjectionTest.TEST_IN_PROPERTY, 
                exchange.getContext().getPropertyValue(ExchangeInjectionTest.TEST_IN_PROPERTY));
        return "";
    }

    @Override
    public String sendReply(String msg) {
        Message message = exchange.createMessage();
        message.setContent(ExchangeInjectionTest.TEST_OUT_CONTENT);
        message.getContext().setProperty(ExchangeInjectionTest.TEST_OUT_PROPERTY, 
                ExchangeInjectionTest.TEST_OUT_PROPERTY);
        exchange.send(message);
        return "THIS SHOULD NOT BE RETURNED AS MESSAGE CONTENT";
    }

    @Override
    public String attachments(String msg) {
        DataSource attachIn = exchange.getMessage().getAttachment(
                ExchangeInjectionTest.TEST_IN_ATTACHMENT);
        Assert.assertNotNull(attachIn);
        Assert.assertEquals(ExchangeInjectionTest.TEST_IN_ATTACHMENT, attachIn.getName());
        Message message = exchange.createMessage();
        message.setContent(ExchangeInjectionTest.TEST_OUT_CONTENT);
        message.addAttachment(ExchangeInjectionTest.TEST_OUT_ATTACHMENT, 
                new DummyDataSource(ExchangeInjectionTest.TEST_OUT_ATTACHMENT));
        exchange.send(message);
        return "";
    }
    
    
}
