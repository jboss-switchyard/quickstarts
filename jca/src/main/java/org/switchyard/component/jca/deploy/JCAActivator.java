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

import org.switchyard.component.jca.JCAMessages;
import org.switchyard.component.jca.JCAConstants;
import org.switchyard.component.jca.config.model.ActivationSpecModel;
import org.switchyard.component.jca.config.model.BatchCommitModel;
import org.switchyard.component.jca.config.model.ConnectionModel;
import org.switchyard.component.jca.config.model.ConnectionSpecModel;
import org.switchyard.component.jca.config.model.EndpointModel;
import org.switchyard.component.jca.config.model.InboundConnectionModel;
import org.switchyard.component.jca.config.model.InboundInteractionModel;
import org.switchyard.component.jca.config.model.InteractionSpecModel;
import org.switchyard.component.jca.config.model.JCABindingModel;
import org.switchyard.component.jca.config.model.ListenerModel;
import org.switchyard.component.jca.config.model.OutboundConnectionModel;
import org.switchyard.component.jca.config.model.OutboundInteractionModel;
import org.switchyard.component.jca.config.model.ProcessorModel;
import org.switchyard.component.jca.config.model.ResourceAdapterModel;
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
            throw JCAMessages.MESSAGES.resourceAdapterRepositoryMustBeInjectedToActivateJCAComponent();
        }

        if (_transactionManager == null) {
            try {
                _transactionManager = (TransactionManager)new InitialContext().lookup(JBOSS_TRANSACTION_MANAGER);
            } catch (NamingException e) {
                throw JCAMessages.MESSAGES.unableToFindTransactionManagerInJNDIAt(JBOSS_TRANSACTION_MANAGER, e);
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
        InboundConnectionModel inboundConnectionModel = jcaconfig.getInboundConnection();
        if (inboundConnectionModel == null) {
            throw JCAMessages.MESSAGES.noInboundConnectionConfigured();
        }
        ResourceAdapterModel resourceAdapterModel = inboundConnectionModel.getResourceAdapter();
        if (resourceAdapterModel == null) {
            throw JCAMessages.MESSAGES.noResourceAdapterConfigured();
        }
        String raName = resourceAdapterModel.getName();
        if (raName == null) {
            throw JCAMessages.MESSAGES.noResourceAdapterNameConfigured();
        }
        String raid = ConnectorServices.getRegisteredResourceAdapterIdentifier(stripDotRarSuffix(raName));
        if (raid == null) {
            throw JCAMessages.MESSAGES.uniqueKeyForResourceAdapter(raName);
        }
        
        Properties raProps = resourceAdapterModel.getProperties();
        Properties activationProps = null;
        ActivationSpecModel activationSpecModel = inboundConnectionModel.getActivationSpec();
        if (activationSpecModel != null) {
            activationProps = activationSpecModel.getProperties();
        }
        InboundInteractionModel inboundInteractionModel = jcaconfig.getInboundInteraction();
        if (inboundInteractionModel == null) {
            throw JCAMessages.MESSAGES.noInboundInteractionConfigured();
        }
        
        String listener = null;
        ListenerModel listenerModel = inboundInteractionModel.getListener();
        if (listenerModel != null) {
            listener = listenerModel.getClassName();
        }
        Class<?> listenerType = null;
        try {
            listenerType = _appClassLoader.loadClass(listener != null ? listener : JCAConstants.DEFAULT_LISTENER_CLASS);
        } catch (Exception e) {
            throw JCAMessages.MESSAGES.noListenerClassFound(listener, e);
        }
        
        EndpointModel endpointModel = inboundInteractionModel.getEndpoint();
        if (endpointModel == null) {
            throw JCAMessages.MESSAGES.noEndpointConfigured();
        }
        String endpointClassName = endpointModel.getEndpointClassName(); 
        Properties endpointProps = endpointModel.getProperties();
        
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
                throw JCAMessages.MESSAGES.listenerTypeIsNotSupportedByResourceAdapter(listenerType.getName(), raName);
            }
            
            Activation activation = listenerContainer.getActivation();
            activationSpec = activation.createInstance();
            if (activationProps != null && !activationProps.isEmpty()) {
                PropertyEditors.mapJavaBeanProperties(activationSpec, activationProps);
            }

            resourceAdapter = _raRepository.getResourceAdapter(raid);
            if (!raProps.isEmpty()) {
                PropertyEditors.mapJavaBeanProperties(resourceAdapter, raProps);
            }
        } catch (Exception e) {
            throw JCAMessages.MESSAGES.couldnTAcquireTheResourceAdapter(raName, e);
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
             throw JCAMessages.MESSAGES.endpointClass(endpointClassName, e);
         }

         boolean transacted = inboundInteractionModel.isTransacted();
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

        BatchCommitModel batchCommit = inboundInteractionModel.getBatchCommit();
        if (transacted && batchCommit != null) {
            inflowMetaData.setUseBatchCommit(true);
            inflowMetaData.setBatchTimeout(batchCommit.getBatchTimeout());
            inflowMetaData.setBatchSize(batchCommit.getBatchSize());
        }

        return new InboundHandler(inflowMetaData, getServiceDomain());
        
    }
    
    private OutboundHandler handleReferenceBinding(JCABindingModel config, QName name) {
        JCABindingModel jcaconfig = (JCABindingModel)config;
        OutboundConnectionModel outboundConnectionModel = jcaconfig.getOutboundConnection();
        if (outboundConnectionModel == null) {
            throw JCAMessages.MESSAGES.noOutboundConnectionConfigured();
        }
        boolean managed = outboundConnectionModel.isManaged();
        if (!managed) {
            throw JCAMessages.MESSAGES.nonManagedScenarioIsNotSupportedYet();
        }
        
        ResourceAdapterModel resourceAdapterModel = outboundConnectionModel.getResourceAdapter();
        if (resourceAdapterModel != null) {
            Properties raProps = resourceAdapterModel.getProperties();
            if (!raProps.isEmpty()) {
                String raName = resourceAdapterModel.getName();
                String raid = ConnectorServices.getRegisteredResourceAdapterIdentifier(stripDotRarSuffix(raName));
                if (raid == null) {
                    throw JCAMessages.MESSAGES.uniqueKeyForResourceAdapter(raName);
                }

                ResourceAdapter resourceAdapter = null;
                try {
                    resourceAdapter = _raRepository.getResourceAdapter(raid);
                    PropertyEditors.mapJavaBeanProperties(resourceAdapter, raProps);
                } catch (Exception e) {
                    throw JCAMessages.MESSAGES.couldnTAcquireTheResourceAdapter(raName, e);
                }
            }
        }

        OutboundInteractionModel outboundInteractionModel = jcaconfig.getOutboundInteraction();
        if (outboundInteractionModel == null) {
            throw JCAMessages.MESSAGES.noOutboundInteractionConfigured();
        }
        ProcessorModel processorModel = outboundInteractionModel.getProcessor();
        if (processorModel == null) {
            throw JCAMessages.MESSAGES.noProcessorConfigured();
        }
        Properties processorProps = processorModel.getProperties();
        AbstractOutboundProcessor processor = null;
        String processorClassName = processorModel.getProcessorClassName();
        Class<?> processorClass = null;
        try {
            processorClass = (Class<?>)_appClassLoader.loadClass(processorClassName);
            processor = (AbstractOutboundProcessor) processorClass.newInstance();
            if (!processorProps.isEmpty()) {
                PropertyEditors.mapJavaBeanProperties(processor, processorProps);
            }
        } catch (Exception e) {
            throw JCAMessages.MESSAGES.outboundProcessorClass(processorClassName, e);
        }
        
        ConnectionModel connectionModel = outboundConnectionModel.getConnection();
        if (connectionModel == null) {
            throw JCAMessages.MESSAGES.noConnectionConfigured();
        }
        String cfJndiName = connectionModel.getConnectionFactoryJNDIName();
        Properties connProps = connectionModel.getProperties();
        processor.setApplicationClassLoader(_appClassLoader)
                    .setMCFProperties(connProps)
                    .setConnectionFactoryJNDIName(cfJndiName)
                    .setJCABindingModel(jcaconfig);

        ConnectionSpecModel connectionSpecModel = outboundInteractionModel.getConnectionSpec();
        if (connectionSpecModel != null) {
            String connSpecClassName = connectionSpecModel.getConnectionSpecClassName();
            Properties connSpecProps = connectionSpecModel.getProperties();
            processor.setConnectionSpec(connSpecClassName, connSpecProps);
        }
        InteractionSpecModel interactionSpecModel = outboundInteractionModel.getInteractionSpec();
        if (interactionSpecModel != null) {
            String interactSpecClassName = interactionSpecModel.getInteractionSpecClassName();
            Properties interactSpecProps = interactionSpecModel.getProperties();
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
