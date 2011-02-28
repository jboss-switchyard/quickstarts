/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.switchyard.component.bean;

import java.lang.reflect.InvocationTargetException;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;

/**
 * Service/Provider proxy handler.
 * <p/>
 * Handler for converting extracting Service operation invocation details from
 * an ESB {@link Exchange}, making the invocation and then mapping the invocation
 * return/result onto the {@link Message Exchange Message} (if the Exchange pattern
 * is {@link ExchangePattern#IN_OUT IN_OUT}).
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ServiceProxyHandler implements ExchangeHandler {

    /**
     * The Service bean instance being proxied to.
     */
    private Object _serviceBean;
    /**
     * The Service bean metadata.
     */
    private BeanServiceMetadata _serviceMetadata;

    /**
     * Public constructor.
     *
     * @param serviceBean     The Service bean instance being proxied to.
     * @param serviceMetadata The Service bean metadata.
     */
    public ServiceProxyHandler(Object serviceBean, BeanServiceMetadata serviceMetadata) {
        this._serviceBean = serviceBean;
        this._serviceMetadata = serviceMetadata;
    }

    /**
     * Called when a message is sent through an exchange.
     *
     * @param exchange an {@code Exchange} instance containing a message to be processed.
     * @throws HandlerException when handling of the message event fails (e.g. invalid request message).
     */
    public void handleMessage(Exchange exchange) throws HandlerException {
        handle(exchange);
    }

    /**
     * Called when a fault is generated while processing an exchange.
     *
     * @param exchange an {@code Exchange} instance containing a message to be processed.
     */
    public void handleFault(Exchange exchange) {
    }

    /**
     * Handle the Service bean invocation.
     *
     * @param exchange The Exchange instance.
     * @throws BeanComponentException Error invoking Bean component operation.
     */
    private void handle(Exchange exchange) throws BeanComponentException {
        Invocation invocation = _serviceMetadata.getInvocation(exchange);

        if (invocation != null) {
            try {
                if (exchange.getContract().getServiceOperation().getExchangePattern() == ExchangePattern.IN_OUT) {
                    Object responseObject = invocation.getMethod().invoke(_serviceBean, invocation.getArgs());
                    Message message = exchange.createMessage();

                    message.setContent(responseObject);
                    exchange.send(message);
                } else {
                    invocation.getMethod().invoke(_serviceBean, invocation.getArgs());
                }
            } catch (IllegalAccessException e) {
                throw new BeanComponentException("Cannot invoke operation '" + invocation.getMethod().getName() + "' on bean component '" + _serviceBean.getClass().getName() + "'.", e);
            } catch (InvocationTargetException e) {
                throw new BeanComponentException("Invocation of operation '" + invocation.getMethod().getName() + "' on bean component '" + _serviceBean.getClass().getName() + "' failed with exception.  See attached cause.", e);
            }
        } else {
            throw new RuntimeException("Unexpected error.  BeanServiceMetadata should return an Invocation instance, or throw a BeanComponentException.");
        }
    }
}
