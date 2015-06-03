/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.switchyard.component.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangeState;
import org.switchyard.ServiceReference;
import org.switchyard.component.bean.deploy.BeanDeploymentMetaData;
import org.switchyard.component.common.SynchronousInOutHandler;
import org.switchyard.extensions.java.JavaService;

/**
 * Client Proxy CDI Bean.
 * <p/>
 * CDI bean for injecting into consumer beans where the {@link org.switchyard.component.bean.Reference @Reference}
 * is used.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ClientProxyBean implements Bean {

    /**
     * The target Service.
     */
    private String _serviceName;

    /**
     * Target service reference.
     */
    private ServiceReference _service;

    /**
     * The bean proxy Interface {@link Class} of the bean being proxied.  This class
     * must be one of the {@link org.switchyard.component.bean.Service @Service}
     * interfaces implemented by the actual Service bean component.
     */
    private Class<?> _serviceInterface;

    /**
     * CDI bean qualifiers.  See CDI Specification.
     */
    private Set<Annotation> _qualifiers;

    /**
     * The dynamic proxy bean instance created from the supplied {@link #_serviceInterface}.
     */
    private Object _proxyBean;

    /**
     * Public constructor.
     *
     * @param serviceName   The name of the ESB Service being proxied to.
     * @param proxyInterface The proxy Interface.
     * @param qualifiers     The CDI bean qualifiers.  Copied from the injection point.
     * @param beanDeploymentMetaData Deployment metadata.
     */
    public ClientProxyBean(String serviceName, Class<?> proxyInterface, Set<Annotation> qualifiers, BeanDeploymentMetaData beanDeploymentMetaData) {
        this._serviceName = serviceName;
        this._serviceInterface = proxyInterface;

        if (qualifiers != null) {
            this._qualifiers = qualifiers;
        } else {
            this._qualifiers = new HashSet<Annotation>();
            this._qualifiers.add(new AnnotationLiteral<Default>() {
            });
            this._qualifiers.add(new AnnotationLiteral<Any>() {
            });
        }

        _proxyBean = Proxy.newProxyInstance(beanDeploymentMetaData.getDeploymentClassLoader(),
                new Class[]{_serviceInterface},
                new ClientProxyInvocationHandler(_serviceInterface));
    }

    /**
     * Get the name of the ESB Service being proxied to.
     *
     * @return The Service name.
     */
    public String getServiceName() {
        return _serviceName;
    }

    /**
     * Get the Service interface.
     * @return The service interface.
     */
    public Class<?> getServiceInterface() {
        return _serviceInterface;
    }

    /**
     * Set the service reference for the target Service.
     * @param service The target service.
     */
    public void setService(ServiceReference service) {
        this._service = service;
    }

    /**
     * Obtains the {@linkplain javax.enterprise.inject bean types} of the bean.
     *
     * @return the {@linkplain javax.enterprise.inject bean types}
     */
    public Set<Type> getTypes() {
        Set<Type> types = new HashSet<Type>();
        types.add(_serviceInterface);
        types.add(Object.class);
        return types;
    }

    /**
     * Obtains the {@linkplain javax.inject.Qualifier qualifiers} of the bean.
     *
     * @return the {@linkplain javax.inject.Qualifier qualifiers}
     */
    public Set<Annotation> getQualifiers() {
        return _qualifiers;
    }

    /**
     * Obtains the {@linkplain javax.enterprise.inject EL name} of a bean, if it has one.
     *
     * @return the {@linkplain javax.enterprise.inject EL name}
     */
    public String getName() {
        return null;
    }

    /**
     * Obtains the {@linkplain javax.enterprise.inject.Stereotype stereotypes}
     * of the bean.
     *
     * @return the set of {@linkplain javax.enterprise.inject.Stereotype stereotypes}
     */
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    /**
     * The bean {@linkplain Class class} of the managed bean or session bean or
     * of the bean that declares the producer method or field.
     *
     * @return the bean {@linkplain Class class}
     */
    public Class<?> getBeanClass() {
        return _serviceInterface;
    }

    /**
     * Determines if the bean is an
     * {@linkplain javax.enterprise.inject.Alternative alternative}.
     *
     * @return <tt>true</tt> if the bean is an
     *         {@linkplain javax.enterprise.inject.Alternative alternative},
     *         and <tt>false</tt> otherwise.
     */
    public boolean isAlternative() {
        return false;
    }

    /**
     * Determines if
     * {@link javax.enterprise.context.spi.Contextual#create(CreationalContext)}
     * sometimes return a null value.
     *
     * @return <tt>true</tt> if the {@code create()} method may return a null
     *         value, and <tt>false</tt> otherwise
     */
    public boolean isNullable() {
        return false;
    }

    /**
     * Obtains the {@link javax.enterprise.inject.spi.InjectionPoint} objects
     * representing injection points of the bean, that will be validated by the
     * container at initialization time.
     *
     * @return the set of {@linkplain javax.enterprise.inject.spi.InjectionPoint injection points} of the bean
     */
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    /**
     * Obtains the {@linkplain javax.enterprise.context scope} of the bean.
     *
     * @return the {@linkplain javax.enterprise.context scope}
     */
    public Class<? extends Annotation> getScope() {
        return ApplicationScoped.class;
    }

    /**
     * Create a new instance of the contextual type. Instances should
     * use the given {@link javax.enterprise.context.spi.CreationalContext}
     * when obtaining contextual references to inject, in order to ensure
     * that any dependent objects are associated with the contextual instance
     * that is being created. An implementation may call
     * {@link javax.enterprise.context.spi.CreationalContext#push(Object)}
     * between instantiation and injection to help the container minimize the
     * use of client proxy objects.
     *
     * @param creationalContext the context in which this instance is being created
     * @return the contextual instance
     */
    public Object create(CreationalContext creationalContext) {
        return _proxyBean;
    }

    /**
     * Destroy an instance of the contextual type. Implementations should
     * call {@link javax.enterprise.context.spi.CreationalContext#release()}
     * to allow the container to destroy dependent objects of the contextual
     * instance.
     *
     * @param instance          the contextual instance to destroy
     * @param creationalContext the context in which this instance was created
     */
    public void destroy(Object instance, CreationalContext creationalContext) {

    }

    /**
     * Dynamic proxy {@link InvocationHandler}.
     */
    private class ClientProxyInvocationHandler implements InvocationHandler {

        private JavaService _invokerInterface;

        public ClientProxyInvocationHandler(Class<?> invokerInterface) {
            _invokerInterface = JavaService.fromClass(invokerInterface);
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (_service == null) {
                throw BeanMessages.MESSAGES.aServiceReferenceToServiceIsNotBoundInto(_serviceName);
            }

            // Handle basic Object methods.
            if (method.getDeclaringClass() == Object.class) {
                final String methodName = method.getName();
                final int paramCount = method.getParameterTypes().length;
                if ("toString".equals(methodName) && paramCount == 0) {
                    return MessageFormat.format("SwitchYard proxy for {0} [{1}]", _invokerInterface.getJavaInterface(), _invokerInterface);
                } else if ("equals".equals(methodName) && paramCount == 1) {
                    return this.equals(args[0]);
                } else if ("hashCode".equals(methodName) && paramCount == 0) {
                    return this.hashCode();
                }
            }

            if (method.getReturnType() != null && !Void.TYPE.isAssignableFrom(method.getReturnType())) {
                SynchronousInOutHandler inOutHandler = new SynchronousInOutHandler();

                Exchange exchangeIn = createExchange(_service, method, inOutHandler);
                // Don't set the message content as an array unless there are multiple arguments
                if (args != null && args.length == 1) {
                    exchangeIn.send(exchangeIn.createMessage().setContent(args[0]));
                } else {
                    exchangeIn.send(exchangeIn.createMessage().setContent(args));
                }

                Exchange exchangeOut = inOutHandler.waitForOut();
                if (exchangeOut.getState() == ExchangeState.OK) {
                    return exchangeOut.getMessage().getContent(method.getReturnType());
                } else {
                    return handleException(exchangeOut, method);
                }
            } else {
                Exchange exchange = createExchange(_service, method, null);
                // Don't set the message content as an array unless there are multiple arguments
                if (args == null) {
                    exchange.send(exchange.createMessage());
                } else if (args.length == 1) {
                    exchange.send(exchange.createMessage().setContent(args[0]));
                } else {
                    exchange.send(exchange.createMessage().setContent(args));
                }

                Object propagateException = _service.getDomain().getProperty(Exchange.PROPAGATE_EXCEPTION_ON_IN_ONLY);
                if ((propagateException == null || Boolean.parseBoolean(propagateException.toString()))
                        && exchange.getState().equals(ExchangeState.FAULT)) {
                    handleException(exchange, method);
                }
                return null;
            }
        }

        private Exchange createExchange(ServiceReference service, Method method, ExchangeHandler responseExchangeHandler) throws BeanComponentException {
            String operationName = method.getName();
            if (service.getInterface().getOperation(operationName) == null) {
                throw BeanMessages.MESSAGES.beanComponentInvocationFailureOperationIsNotDefinedOnService(operationName, _serviceName);
            }

            return service.createExchange(operationName, responseExchangeHandler);
        }

        private Throwable handleException(Exchange exchange, Method method) throws Throwable {
            Object exceptionObj = exchange.getMessage().getContent();
            if (exceptionObj instanceof Throwable) {
                if (exceptionObj instanceof BeanComponentException) {
                    BeanComponentException beanCompException = (BeanComponentException) exceptionObj;
                    Throwable cause = beanCompException.getCause();
                    if (cause instanceof InvocationTargetException) {
                        throw cause.getCause();
                    } else {
                        throw cause;
                    }
                }
                throw (Throwable) exceptionObj;
            } else {
                throw BeanMessages.MESSAGES.beanComponentInvocationFailureServiceOperation(_serviceName, method.getName()).setFaultExchange(exchange);
            }
        }
        
    }
}
