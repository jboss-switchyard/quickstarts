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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.MessageBuilder;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.internal.ServiceDomains;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ClientProxyBean implements Bean {

    private QName serviceQName;
    private Class<?> beanClass;
    private Set<Annotation> qualifiers;
    private Object proxyBean;

    public ClientProxyBean(QName serviceQName, Class<?> beanClass, Set<Annotation> qualifiers) {
        this.serviceQName = serviceQName;
        this.beanClass = beanClass;

        if(qualifiers != null) {
            this.qualifiers = qualifiers;
        } else {
            this.qualifiers = new HashSet<Annotation>();
            this.qualifiers.add(new AnnotationLiteral<Default>() {});
            this.qualifiers.add(new AnnotationLiteral<Any>() {});
        }

        proxyBean = Proxy.newProxyInstance(ClientProxyBean.class.getClassLoader(),
                                          new Class[] { beanClass },
                                          new ClientProxyInvocationHandler());
    }

    public QName getServiceQName() {
        return serviceQName;
    }

    public Set<Type> getTypes() {
        Set<Type> types = new HashSet<Type>();
        types.add(beanClass);
        types.add(Object.class);
        return types;
    }

    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    public String getName() {
        // TODO: Can we take this from the Bean instance associated with the actual service... think that may cause a duplicate bean name bean resolution issue
        return null;
    }

    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public boolean isAlternative() {
        return false;
    }

    public boolean isNullable() {
        return false;
    }

    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    public Class<? extends Annotation> getScope() {
        return ApplicationScoped.class;
    }

    public Object create(CreationalContext creationalContext) {
        return proxyBean;
    }

    public void destroy(Object instance, CreationalContext creationalContext) {
      
    }

    private class ClientProxyInvocationHandler implements InvocationHandler {

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            ServiceDomain domain = ServiceDomains.getDomain();

            if(method.getReturnType() != null && method.getReturnType() != Void.class) {
                final BlockingQueue<Exchange> responseQueue = new ArrayBlockingQueue<Exchange>(1);

                ExchangeHandler responseExchangeHandler = new ExchangeHandler() {
                    public void handleMessage(Exchange exchange) throws HandlerException {
                        responseQueue.offer(exchange);
                    }

                    public void handleFault(Exchange exchange) {
                        // TODO: properly handle fault
                        responseQueue.offer(exchange);
                    }
                };

                Exchange exchangeIn = domain.createExchange(serviceQName, ExchangePattern.IN_OUT, responseExchangeHandler);

                Message sendMessage = prepareSend(exchangeIn, args, method);
                exchangeIn.send(sendMessage, exchangeIn.getContext(Scope.MESSAGE));

                Exchange exchangeOut = responseQueue.take();
                Object responseObject = exchangeOut.getMessage().getContent();
                
                return responseObject;
            } else {
                Exchange exchange = domain.createExchange(serviceQName, ExchangePattern.IN_ONLY, null);

                Message sendMessage = prepareSend(exchange, args, method);
                exchange.send(sendMessage);

                return null;
            }
        }

        private Message prepareSend(Exchange exchange, Object[] args, Method method) {
            BeanServiceMetadata.setOperationName(exchange, method.getName());
            Message inMessage = MessageBuilder.newInstance().buildMessage();
            inMessage.setContent(args);
            return inMessage;
        }

    }
}
