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
package org.switchyard.component.rules.event.drools;

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.ServiceReference;
import org.switchyard.component.rules.config.model.ChannelModel;
import org.switchyard.metadata.BaseExchangeContract;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.ServiceOperation;

/**
 * A Drools Channel that can send to a SwitchYard Service (IN ONLY!).
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class SwitchYardServiceChannel implements SwitchYardChannel {

    private ChannelModel _model;
    private ExchangeContract _contract;
    private ServiceReference _reference;

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelModel getModel() {
        return _model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SwitchYardChannel setModel(ChannelModel model) {
        _model = model;
        if (_model != null) {
            String oper = _model.getOperation();
            if (oper != null) {
                ServiceOperation operation;
                QName input = _model.getInput();
                if (input != null) {
                    operation = new InOnlyOperation(oper, input);
                } else {
                    operation = new InOnlyOperation(oper);
                }
                _contract = new BaseExchangeContract(operation);
            } else {
                _contract = ExchangeContract.IN_ONLY;
            }
        } else {
            _contract = null;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceReference getReference() {
        return _reference;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SwitchYardChannel setReference(ServiceReference reference) {
        _reference = reference;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(Object object) {
        Exchange exchange = _reference.createExchange(_contract);
        Message message = exchange.createMessage();
        message.setContent(object);
        exchange.send(message);
    }

}
