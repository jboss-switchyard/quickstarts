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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.ServiceReference;
import org.switchyard.SwitchYardException;
import org.switchyard.common.property.PropertyResolver;
import org.switchyard.common.type.reflect.FieldAccess;
import org.switchyard.component.bean.deploy.BeanDeploymentMetaData;
import org.switchyard.component.bean.internal.context.ContextProxy;
import org.switchyard.component.bean.internal.message.MessageProxy;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.deploy.ComponentNames;
import org.switchyard.deploy.ServiceHandler;

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
public class ServiceProxyHandler extends BaseServiceHandler implements ServiceHandler {

    private static Logger _logger = Logger.getLogger(ServiceProxyHandler.class);

    /**
     * The Service bean instance being proxied to.
     */
    private Object _serviceBean;
    /**
     * The Service bean metadata.
     */
    private BeanServiceMetadata _serviceMetadata;
    /**
     * Deployment metadata.
     */
    private BeanDeploymentMetaData _beanDeploymentMetaData;
    
    private Map<String, ServiceReference> _references = 
            new HashMap<String, ServiceReference>();

    /**
     * Public constructor.
     *
     * @param serviceBean     The Service bean instance being proxied to.
     * @param serviceMetadata The Service bean metadata.
     * @param beanDeploymentMetaData Deployment metadata.
     */
    public ServiceProxyHandler(Object serviceBean,
            BeanServiceMetadata serviceMetadata, 
            BeanDeploymentMetaData beanDeploymentMetaData) {
        _serviceBean = serviceBean;
        _serviceMetadata = serviceMetadata;
        _beanDeploymentMetaData = beanDeploymentMetaData;
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
     * Add the specified reference to the handler.
     * @param reference service reference
     */
    public void addReference(ServiceReference reference) {
        QName refName = ComponentNames.unqualify(reference);
        _references.put(refName.getLocalPart(), reference);
    }

    /**
     * Inject Implementation Properties into Bean component.
     * @param resolver property resolver
     */
    public void injectImplementationProperties(PropertyResolver resolver) {
        for (Field field : _serviceBean.getClass().getDeclaredFields()) {
            Property propAnno = field.getAnnotation(Property.class);
            if (propAnno != null) {
                Object property = resolver.resolveProperty(propAnno.name());
                if (property != null) {
                    if (field.getType().isAssignableFrom(property.getClass())) {
                        new FieldAccess<Object>(field).write(_serviceBean, property);
                    } else {
                        _logger.warn("Property '" + propAnno.name() + "' has incompatible type: Bean '" + _serviceMetadata.getServiceClass().getName()
                                + "' is expecting '" + field.getType().getName() + "', but was '" + property.getClass().getName() + "'. ignoring...");
                    }
                }
                
            }
        }
    }
    
    protected ClassLoader getDeploymentClassLoader() {
        return _beanDeploymentMetaData.getDeploymentClassLoader();
    }

    /**
     * Handle the Service bean invocation.
     *
     * @param exchange The Exchange instance.
     * @throws HandlerException Error invoking Bean component operation.
     */
    private void handle(Exchange exchange) throws HandlerException {
        Invocation invocation = _serviceMetadata.getInvocation(exchange);

        if (invocation != null) {
            ExchangePattern exchangePattern = exchange.getContract().getProviderOperation().getExchangePattern();
            try {

                if (_logger.isDebugEnabled()) {
                    _logger.debug("CDI Bean Service ExchangeHandler proxy class received " + exchangePattern + " Exchange ("
                            + System.identityHashCode(exchange) + ") for Bean Service '"
                            + exchange.getProvider().getName() + "'.  Invoking bean method '" + invocation.getMethod().getName() + "'.");
                }

                Object responseObject;
                ContextProxy.setContext(exchange.getContext());
                MessageProxy.setMessage(exchange.getMessage());
                try {
                    responseObject = invocation.getMethod().invoke(_serviceBean, invocation.getArgs());
                } finally {
                    ContextProxy.setContext(null);
                    MessageProxy.setMessage(null);
                }
                
                if (exchangePattern == ExchangePattern.IN_OUT) {
                    Message message = exchange.createMessage();
                    message.setContent(responseObject);
                    exchange.send(message);
                }
            } catch (Exception ex) {
                String errMsg = "Invocation of operation '" + invocation.getMethod().getName() 
                        + "' on bean component '" + _serviceBean.getClass().getName() + "failed.";
                // write error details to log
                if (_logger.isDebugEnabled()) {
                    _logger.debug(errMsg, ex);
                }
                
                // if the exception is declared on service interface, use sendFault, otherwise throw an exception
                Throwable faultContent = ex;
                if (faultContent instanceof InvocationTargetException) {
                    faultContent = ((InvocationTargetException)ex).getTargetException();
                }
                if (exchangePattern == ExchangePattern.IN_OUT) {
                    for (Class<?> expectedFault : invocation.getMethod().getExceptionTypes()) {
                        if (expectedFault.isAssignableFrom(faultContent.getClass())) {
                            exchange.sendFault(exchange.createMessage().setContent(faultContent));
                            return;
                        }
                    }
                }
                throw new HandlerException(faultContent);
            }
        } else {
            throw new SwitchYardException("Unexpected error.  BeanServiceMetadata should return an Invocation instance, or throw a BeanComponentException.");
        }
    }

    @Override
    protected void doStart() {
        // Initialise any client proxies to the started service...
        for (ClientProxyBean proxyBean : _beanDeploymentMetaData.getClientProxies()) {
            if (_references.containsKey(proxyBean.getServiceName())) {
                proxyBean.setService(_references.get(proxyBean.getServiceName()));
            }
        }
    }

    @Override
    public void stop() {
        // NOP
        // leave state alone
    }
}
