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
package org.switchyard.component.hornetq.deploy;

import java.util.Set;

import org.apache.log4j.Logger;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.MessageHandler;
import org.hornetq.api.core.client.ServerLocator;
import org.switchyard.Exchange;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.hornetq.composer.HornetQBindingData;
import org.switchyard.component.common.selector.OperationSelector;
import org.switchyard.component.common.selector.OperationSelectorFactory;
import org.switchyard.component.hornetq.composer.HornetQComposition;
import org.switchyard.component.hornetq.config.model.HornetQBindingModel;
import org.switchyard.component.hornetq.config.model.HornetQConfigModel;
import org.switchyard.component.hornetq.internal.HornetQUtil;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.metadata.ServiceOperation;

/**
 * A HornetQ inbound handler is a HornetQ MessageHandler that handles messages from a HornetQ
 * queue and invokes a SwitchYard service with the message's contents.
 * 
 * @author Daniel Bevenius
 *
 */
public class InboundHandler extends BaseServiceHandler implements MessageHandler {
    
    private Logger _logger = Logger.getLogger(InboundHandler.class);

    private final HornetQBindingModel _bindingModel;
    private final HornetQConfigModel _configModel;
    private final MessageComposer<HornetQBindingData> _messageComposer;
    private final OperationSelector<HornetQBindingData> _operationSelector;
    private ClassLoader _applicationClassLoader;
    private ServiceReference _serviceRef;
    private ServiceDomain _domain;
    private ServerLocator _serverLocator;
    private ClientSessionFactory _factory;
    private ClientSession _session;
    private ClientConsumer _consumer;

    /**
     * Sole constructor that takes a {@link HornetQBindingModel}.
     * 
     * @param hbm the {@link HornetQBindingModel} containing the configuration for this handler.
     * @param serverLocator the HornetQServer locator used to interact with HornetQ.
     * @param domain service domain
     */
    public InboundHandler(final HornetQBindingModel hbm, final ServerLocator serverLocator, ServiceDomain domain) {
        _applicationClassLoader = Thread.currentThread().getContextClassLoader();
        _bindingModel = hbm;
        _configModel = hbm.getHornetQConfig();
        _messageComposer = HornetQComposition.getMessageComposer(hbm);
        _operationSelector = OperationSelectorFactory
                                .getOperationSelectorFactory(HornetQBindingData.class)
                                .newOperationSelector(hbm.getOperationSelector());
        _domain = domain;
        _serverLocator = serverLocator;
    }

    /**
     * Starts this InboundHandler which involves setting up this inbound handler as 
     * a HornetQ MessageHandler so that messgages that arrive on the configured destination
     * will be passed to the SwitchYard {@link ServiceReference}.
     */
    public void start() {
        _serviceRef = _domain.getServiceReference(_bindingModel.getService().getQName());
        try {
            _factory =  _serverLocator.createSessionFactory();
            _session = _configModel.isXASession() ? _factory.createXASession() : _factory.createSession();
            _consumer = _session.createConsumer(_configModel.getQueue());
            _consumer.setMessageHandler(this);
            _session.start();
        } catch (final Exception e) {
            throw new SwitchYardException(e);
        }
    }
    
    /**
     * Closes the resources opened by this instance. This includes the consumer, session, session factory, 
     * and the server locator.
     */
    public void stop() {
        HornetQUtil.closeClientConsumer(_consumer);
        HornetQUtil.closeSession(_session);
        HornetQUtil.closeSessionFactory(_factory);
        HornetQUtil.closeServerLocator(_serverLocator);
    }
    
    @Override
    public void onMessage(final ClientMessage message) {
        final ClassLoader origCl = Thread.currentThread().getContextClassLoader();
        try {
            HornetQBindingData bindingData = new HornetQBindingData(message);
            Exchange exchange = _serviceRef.createExchange(getOperationName(bindingData), this);
            Thread.currentThread().setContextClassLoader(_applicationClassLoader);
            _logger.info("onMessage :" + message);
            exchange.send(_messageComposer.compose(bindingData, exchange, true));
        } catch (final Exception e) {
            throw new SwitchYardException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(origCl);
        }
    }
    
    private String getOperationName(HornetQBindingData message) throws Exception {
        String operationName = null;
        if (_operationSelector != null) {
            operationName = _operationSelector.selectOperation(message).getLocalPart();
        }
        
        if (operationName == null) {
            final Set<ServiceOperation> operations = _serviceRef.getInterface().getOperations();
            if (operations.size() != 1) {
                final StringBuilder msg = new StringBuilder();
                msg.append("No operationSelector was configured for the HornetQ Component and the Service Interface ");
                msg.append("contains more than one operation: ").append(operations);
                msg.append("Please add an operationSelector element.");
                throw new SwitchYardException(msg.toString());
            }
            final ServiceOperation serviceOperation = operations.iterator().next();
            operationName = serviceOperation.getName();
        }
        
        return operationName;
        
    }
}
