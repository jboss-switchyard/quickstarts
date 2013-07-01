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
import org.switchyard.component.jca.config.model.BatchCommitModel;
import org.switchyard.component.jca.config.model.JCABindingModel;
import org.switchyard.component.jca.endpoint.AbstractInflowEndpoint;
import org.switchyard.component.jca.processor.AbstractOutboundProcessor;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.ServiceHandler;

import org.jboss.jca.core.spi.rar.Activation;
import org.jboss.jca.core.spi.rar.MessageListener;
import org.jboss.jca.core.spi.rar.ResourceAdapterRepository;
import org.jboss.util.propertyeditor.PropertyEditors;
import org.jboss.as.connector.util.ConnectorServices;


/**
 * Activator for JCA message inflow binding.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class JCAActivator extends BaseActivator {

    private static final String JBOSS_TRANSACTION_MANAGER = "java:jboss/TransactionManager";

    static final String[] TYPES = new String[] {"jca"};
    
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
            throw new IllegalArgumentException("Unique key for ResourceAdapter '" + raName + "' couldn't be found.");
        }
        
        Properties raProps = jcaconfig.getInboundConnection().getResourceAdapter().getProperties();
        Properties activationProps = jcaconfig.getInboundConnection().getActivationSpec().getProperties();
        String listener = jcaconfig.getInboundInteraction().getListener().getClassName();
        String endpointClassName = jcaconfig.getInboundInteraction().getEndpoint().getEndpointClassName(); 
        Properties endpointProps = jcaconfig.getInboundInteraction().getEndpoint().getProperties();
        
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
            if (!activationProps.isEmpty()) {
                PropertyEditors.mapJavaBeanProperties(activationSpec, activationProps);
            }

            resourceAdapter = _raRepository.getResourceAdapter(raid);
            if (!raProps.isEmpty()) {
                PropertyEditors.mapJavaBeanProperties(resourceAdapter, raProps);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Couldn't acquire the ResourceAdapter '" + raName + "'.", e);
        }
        
         AbstractInflowEndpoint endpoint = null;
         Class<?> endpointClass = null;
         try {
             endpointClass = (Class<?>)_appClassLoader.loadClass(endpointClassName);
             endpoint = (AbstractInflowEndpoint) endpointClass.newInstance();
             if (!endpointProps.isEmpty()) {
                 PropertyEditors.mapJavaBeanProperties(endpoint, endpointProps);
             }
         } catch (Exception e) {
             throw new IllegalArgumentException("Endpoint class '" + endpointClassName + "' couldn't be instantiated.", e);
         }

         boolean transacted = jcaconfig.getInboundInteraction().isTransacted();
         endpoint.setApplicationClassLoader(_appClassLoader)
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

        BatchCommitModel batchCommit = jcaconfig.getInboundInteraction().getBatchCommit();
        if (transacted && batchCommit != null) {
            inflowMetaData.setUseBatchCommit(true);
            inflowMetaData.setBatchTimeout(batchCommit.getBatchTimeout());
            inflowMetaData.setBatchSize(batchCommit.getBatchSize());
        }

        return new InboundHandler(inflowMetaData, getServiceDomain());
        
    }
    
    private OutboundHandler handleReferenceBinding(JCABindingModel config, QName name) {
        JCABindingModel jcaconfig = (JCABindingModel)config;
        boolean managed = jcaconfig.getOutboundConnection().isManaged();
        if (!managed) {
            throw new IllegalArgumentException("Non-Managed Scenario is not supported yet");
        }
        
        Properties raProps = jcaconfig.getOutboundConnection().getResourceAdapter().getProperties();
        String raName = jcaconfig.getOutboundConnection().getResourceAdapter().getName();
        String raid = ConnectorServices.getRegisteredResourceAdapterIdentifier(stripDotRarSuffix(raName));
        if (raid == null) {
            throw new IllegalArgumentException("Unique key for ResourceAdapter '" + raName + "' couldn't be found.");
        }
        
        ResourceAdapter resourceAdapter = null;
        try {
            resourceAdapter = _raRepository.getResourceAdapter(raid);
            if (!raProps.isEmpty()) {
                PropertyEditors.mapJavaBeanProperties(resourceAdapter, raProps);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Couldn't acquire the ResourceAdapter '" + raName + "'.", e);
            
        }

        Properties processorProps = jcaconfig.getOutboundInteraction().getProcessor().getProperties();
        AbstractOutboundProcessor processor = null;
        String processorClassName = jcaconfig.getOutboundInteraction().getProcessor().getProcessorClassName();
        Class<?> processorClass = null;
        try {
            processorClass = (Class<?>)_appClassLoader.loadClass(processorClassName);
            processor = (AbstractOutboundProcessor) processorClass.newInstance();
            if (!processorProps.isEmpty()) {
                PropertyEditors.mapJavaBeanProperties(processor, processorProps);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Outbound Processor class '" + processorClassName + "' couldn't be instantiated.", e);
        }
        
        String cfJndiName = jcaconfig.getOutboundConnection().getConnection().getConnectionFactoryJNDIName();
        Properties connProps = jcaconfig.getOutboundConnection().getConnection().getProperties();
        processor.setApplicationClassLoader(_appClassLoader)
                    .setMCFProperties(connProps)
                    .setConnectionFactoryJNDIName(cfJndiName)
                    .setJCABindingModel(jcaconfig);

        if (jcaconfig.getOutboundInteraction().getConnectionSpec() != null) {
            String connSpecClassName = jcaconfig.getOutboundInteraction().getConnectionSpec().getConnectionSpecClassName();
            Properties connSpecProps = jcaconfig.getOutboundInteraction().getConnectionSpec().getProperties();
            processor.setConnectionSpec(connSpecClassName, connSpecProps);
        }
        if (jcaconfig.getOutboundInteraction().getInteractionSpec() != null) {
            String interactSpecClassName = jcaconfig.getOutboundInteraction().getInteractionSpec().getInteractionSpecClassName();
            Properties interactSpecProps = jcaconfig.getOutboundInteraction().getInteractionSpec().getProperties();
            processor.setInteractionSpec(interactSpecClassName, interactSpecProps);
        }

        return new OutboundHandler(processor, getServiceDomain());
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
