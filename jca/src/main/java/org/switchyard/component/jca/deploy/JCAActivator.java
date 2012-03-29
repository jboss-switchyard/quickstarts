/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.jca.deploy;

import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.ResourceAdapter;
import javax.transaction.TransactionManager;

import org.switchyard.component.jca.JCAConstants;
import org.switchyard.component.jca.config.model.JCABindingModel;
import org.switchyard.component.jca.endpoint.AbstractInflowEndpoint;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.ServiceHandler;

import org.jboss.jca.core.spi.rar.Activation;
import org.jboss.jca.core.spi.rar.MessageListener;
import org.jboss.jca.core.spi.rar.ResourceAdapterRepository;
import org.jboss.util.propertyeditor.PropertyEditors;
import org.jboss.as.connector.ConnectorServices;


/**
 * Activator for JCA message inflow binding.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class JCAActivator extends BaseActivator {

    private static final String JBOSS_TRANSACTION_MANAGER = "java:jboss/TransactionManager";

    private static final String[] TYPES = new String[] {"jca"};
    
    private final ClassLoader _appClassLoader;
    
    private TransactionManager _transactionManager;
    
    private ResourceAdapterRepository _raRepository;

    /**
     * Sole constructor .
     */
    public JCAActivator() {
        super(TYPES);
        _appClassLoader = Thread.currentThread().getContextClassLoader();
    }
    
    @Override
    public ServiceHandler activateBinding(QName name, BindingModel config) {
        if (_raRepository == null) {
            throw new IllegalStateException("ResourceAdapterRepository must be injected to activate JCA component.");
        }

        if (_transactionManager == null) {
            try {
                _transactionManager = (TransactionManager)new InitialContext().lookup(JBOSS_TRANSACTION_MANAGER);
            } catch (NamingException e) {
                throw new IllegalArgumentException("Unable to find TransactionManager in JNDI at " + JBOSS_TRANSACTION_MANAGER, e);
            }
        }

        if (config.isServiceBinding()) {
            return handleServiceBinding((JCABindingModel)config, name);
        } else {
            return handleReferenceBinding((JCABindingModel)config, name);
        }
    }

    @Override
    public void deactivateBinding(QName name, ServiceHandler handler) {
        // Nothing to do here
    }
    
    /**
     * set ResourceAdapterRepository.
     * 
     * @param repository ResourceAdapterRepository to set
     */
    public void setResourceAdapterRepository(ResourceAdapterRepository repository) {
        _raRepository = repository;
    }
    
    private InboundHandler handleServiceBinding(JCABindingModel config, QName name) {
        
        JCABindingModel jcaconfig = (JCABindingModel)config;
        String raName = jcaconfig.getInboundConnection().getResourceAdapter().getName();
        String raid = ConnectorServices.getRegisteredResourceAdapterIdentifier(stripDotRarSuffix(raName));
        if (raid == null) {
            throw new IllegalArgumentException("ResourceAdapter " + raName + " couldn't be found.");
        }
        
        Properties activationProps = jcaconfig.getInboundConnection().getActivationSpec().getProperties();
        String listener = jcaconfig.getInboundInteraction().getListener().getClassName();
        String operationName = jcaconfig.getInboundInteraction().getInboundOperation().getName();
        String endpointClassName = jcaconfig.getInboundInteraction().getEndpointClassName(); 
        
        Class<?> listenerType = null;
        try {
            listenerType = _appClassLoader.loadClass(listener != null ? listener : JCAConstants.DEFAULT_LISTENER_CLASS);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        
        ActivationSpec activationSpec = null;
        ResourceAdapter resourceAdapter = null;
        MessageListener listenerContainer = null;
        try {
            List<MessageListener> listeners = _raRepository.getMessageListeners(raid);
            for (MessageListener l : listeners) {
                if (listenerType.equals(l.getType())) {
                    listenerContainer = l;
                }
            }
            if (listenerContainer == null) {
                throw new IllegalArgumentException("listener type " + listenerType.getName()
                        + " is not supported by ResourceAdapter " + raName);
            }
            
            Activation activation = listenerContainer.getActivation();
            activationSpec = activation.createInstance();
            PropertyEditors.mapJavaBeanProperties(activationSpec, activationProps);
            resourceAdapter = _raRepository.getResourceAdapter(raid);
        } catch (Exception e) {
            throw new IllegalArgumentException("Couldn't acquire the ResourceAdapter '" + raName + "'.", e);
        }
        
         AbstractInflowEndpoint endpoint = null;
         Class<?> endpointClass = null;
         try {
             endpointClass = (Class<?>)_appClassLoader.loadClass(endpointClassName);
             endpoint = (AbstractInflowEndpoint) endpointClass.newInstance();
         } catch (Exception e) {
             throw new IllegalArgumentException("Endpoint class '" + endpointClassName + "' couldn't be instantiated.", e);
         }

         boolean transacted = jcaconfig.getInboundInteraction().isTransacted();
         endpoint.setOperationName(operationName)
                     .setServiceDomain(getServiceDomain())
                     .setServiceQName(name)
                     .setDeliveryTransacted(transacted)
                     .setJCABindingModel(jcaconfig);

        JCAInflowDeploymentMetaData inflowMetaData = new JCAInflowDeploymentMetaData()
                                                        .setActivationSpec(activationSpec)
                                                        .setApplicationClassLoader(_appClassLoader)
                                                        .setListenerInterface(listenerType)
                                                        .setMessageEndpoint(endpoint)
                                                        .setResourceAdapter(resourceAdapter)
                                                        .setTransactionManager(_transactionManager)
                                                        .setDeliveryTransacted(transacted);
        return new InboundHandler(inflowMetaData);
        
    }
    
    private OutboundHandler handleReferenceBinding(JCABindingModel config, QName name) {
        // TODO support reference binding on JCA outbound
        return new OutboundHandler();
    }
    
    private String stripDotRarSuffix(final String raName) {
        if (raName == null) {
            return null;
        }
        // See RaDeploymentParsingProcessor
        if (raName.endsWith(".rar")) {
            return raName.substring(0, raName.indexOf(".rar"));
        }
        return raName;
    }
    
}
