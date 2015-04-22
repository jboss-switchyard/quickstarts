package org.switchyard.component.bean.exchange;

public interface ExchangeInjection {

    String injectionNotNull(String msg);
    String correctMessage(String msg);
    String sendReply(String msg);
    String attachments(String msg);
}
