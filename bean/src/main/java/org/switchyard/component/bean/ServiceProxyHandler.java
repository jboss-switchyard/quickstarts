/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.component.bean;

import java.lang.reflect.InvocationTargetException;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.MessageBuilder;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ServiceProxyHandler implements ExchangeHandler {

    private Object serviceBean;
    private BeanServiceMetadata serviceMetadata;

    public ServiceProxyHandler(Object serviceBean, BeanServiceMetadata serviceMetadata) {
        this.serviceBean = serviceBean;
        this.serviceMetadata = serviceMetadata;
    }

    public void handleMessage(Exchange exchange) throws HandlerException {
        handle(exchange);
    }

    public void handleFault(Exchange exchange) {
        handle(exchange);
    }

    private void handle(Exchange exchange) {
        BeanServiceMetadata.Invocation invocation = serviceMetadata.getInvocation(exchange);

        if(invocation != null) {
            try {
                if(exchange.getPattern() == ExchangePattern.IN_OUT) {
                    Object responseObject = invocation.getMethod().invoke(serviceBean, invocation.getArgs());
                    Message message = MessageBuilder.newInstance().buildMessage();

                    message.setContent(responseObject);
                    exchange.send(message);
                } else {
                    invocation.getMethod().invoke(serviceBean, invocation.getArgs());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                // TODO: sendFault...
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                // TODO: sendFault...
            }
        } else {
            System.out.println("Unable to resolve invocation parameters.");
            // TODO: sendFault...
        }
    }
}
