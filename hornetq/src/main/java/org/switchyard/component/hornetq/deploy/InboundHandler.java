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

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.MessageHandler;
import org.hornetq.api.core.client.ServerLocator;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.HandlerException;
import org.switchyard.ServiceReference;
import org.switchyard.component.hornetq.composer.HornetQComposition;
import org.switchyard.component.hornetq.config.model.HornetQBindingModel;
import org.switchyard.component.hornetq.config.model.HornetQConfigModel;
import org.switchyard.component.hornetq.config.model.OperationSelector;
import org.switchyard.component.hornetq.internal.HornetQUtil;
import org.switchyard.composer.MessageComposer;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.metadata.BaseExchangeContract;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.ServiceOperation;

/**
 * A HornetQ inbound handler is a HornetQ MessageHandler that handles messages from a HornetQ
 * queue and invokes a SwitchYard service with the message's contents.
 * 
 * @author Daniel Bevenius
 *
 */
public class InboundHandler implements ExchangeHandler, MessageHandler {
    
    private Logger _logger = Logger.getLogger(InboundHandler.class);

    private final HornetQBindingModel _bindingModel;
    private final HornetQConfigModel _configModel;
    private final MessageComposer<ClientMessage> _messageComposer;
    private ServiceReference _serviceRef;
    private ServerLocator _serverLocator;
    private ClientSessionFactory _factory;
    private ClientSession _session;
    private ClientConsumer _consumer;

    /**
     * Sole constructor that takes a {@link HornetQBindingModel}.
     * 
     * @param hbm the {@link HornetQBindingModel} containing the configuration for this handler.
     * @param serverLocator the HornetQServer locator used to interact with HornetQ. 
     */
    public InboundHandler(final HornetQBindingModel hbm, final ServerLocator serverLocator) {
        _bindingModel = hbm;
        _configModel = hbm.getHornetQConfig();
        _messageComposer = HornetQComposition.getMessageComposer(hbm);
        _serverLocator = serverLocator;
    }

    /**
     * Starts this InboundHandler which involves setting up this inbound handler as 
     * a HornetQ MessageHandler so that messgages that arrive on the configured destination
     * will be passed to the SwitchYard {@link ServiceReference}.
     * 
     * @param serviceRef the SwitchYard service that the message contents should be sent to.
     */
    public void start(final ServiceReference serviceRef) {
        _serviceRef = serviceRef;
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
     * 
     * @param serviceRef the service reference that is the target of this inbound handler.
     */
    public void stop(final ServiceReference serviceRef) {
        HornetQUtil.closeClientConsumer(_consumer);
        HornetQUtil.closeSession(_session);
        HornetQUtil.closeSessionFactory(_factory);
        HornetQUtil.closeServerLocator(_serverLocator);
    }
    
    @Override
    public void onMessage(final ClientMessage message) {
        final Exchange exchange = createExchange(_serviceRef);
        try {
            _logger.info("onMessage :" + message);
            exchange.send(_messageComposer.compose(message, exchange, true));
        } catch (final Exception e) {
            throw new SwitchYardException(e);
        }
    }
    
    private Exchange createExchange(final ServiceReference serviceReference) {
        final String operationName = getOperationName();
        final QName inputType = getOperationInputType(serviceReference, operationName);
        final InOnlyOperation inOnlyOperation = new InOnlyOperation(operationName, inputType);
        final BaseExchangeContract contract = new BaseExchangeContract(inOnlyOperation);
        return serviceReference.createExchange(contract);
    }
    
    private String getOperationName() {
        final OperationSelector operationSelector = _bindingModel.getOperationSelector();
        if (operationSelector != null) {
            return operationSelector.getOperationName();
        }
        return null;
    }
    
    private QName getOperationInputType(final ServiceReference ref, final String operationName) {
        final ServiceOperation operation = ref.getInterface().getOperation(operationName);
        if (operation != null) {
            return operation.getInputType();
        }
        
        throw new SwitchYardException("The operationName [" + operationName + "] was not found on the" 
                + " in the set of operations of the interface " + ref.getInterface().getOperations() 
                + ". Please check that the 'operationName' attribute specified in the HornetQ binding "
                + " matches an operation on the interface.");
    }

    @Override
    public void handleMessage(final Exchange exchange) throws HandlerException {
        
    }

    @Override
    public void handleFault(final Exchange exchange) {
    }

}
