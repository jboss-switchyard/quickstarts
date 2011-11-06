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

package org.switchyard;

import javax.xml.namespace.QName;

import org.switchyard.metadata.ExchangeContract;

/**
 * Dumb implementation of Exchange meant for unit testing. 
 */
public class MockExchange implements Exchange {
    
    private ExchangeState _state;
    private ExchangePhase _phase;
    private QName _serviceName;
    private ExchangeContract _contract;
    private Message _message;
    private Context _context;
    
    public MockExchange() {
    }

    @Override
    public Message createMessage() {
        return _message;
    }

    @Override
    public Context getContext() {
        return _context;
    }

    @Override
    public ExchangeContract getContract() {
        return _contract;
    }

    @Override
    public Message getMessage() {
        return _message;
    }

    @Override
    public ExchangePhase getPhase() {
        return _phase;
    }

    @Override
    public QName getServiceName() {
        return _serviceName;
    }

    @Override
    public ExchangeState getState() {
        return _state;
    }

    @Override
    public void send(Message message) {
        // NOP
    }

    @Override
    public void sendFault(Message message) {
        // NOP
    }

    public MockExchange setPhase(ExchangePhase phase) {
        _phase = phase;
        return this;
    }
    
    public MockExchange setState(ExchangeState state) {
        _state = state;
        return this;
    }
    
    public MockExchange setServiceName(QName serviceName) {
        _serviceName = serviceName;
        return this;
    }
    
    public MockExchange setMessage(Message message) {
        _message = message;
        return this;
    }
    
    public MockExchange setContext(Context context) {
        _context = context;
        return this;
    }
    
    public MockExchange setContract(ExchangeContract contract) {
        _contract = contract;
        return this;
    }
}
