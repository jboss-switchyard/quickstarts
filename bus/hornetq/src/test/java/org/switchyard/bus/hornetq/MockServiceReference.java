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

package org.switchyard.bus.hornetq;

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceReference;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.ServiceInterface;

public class MockServiceReference implements ServiceReference {
    
    private QName _serviceName;
    private ServiceInterface _serviceInterface;
    
    public MockServiceReference(QName serviceName) {
        this(serviceName, new InOnlyService());
    }
    
    public MockServiceReference(QName serviceName, ServiceInterface serviceInterface) {
        _serviceName = serviceName;
        _serviceInterface = serviceInterface;
    }

    @Override
    public Exchange createExchange(ExchangeContract contract) {
        return null;
    }

    @Override
    public Exchange createExchange(ExchangeContract contract,
            ExchangeHandler handler) {
        return null;
    }

    @Override
    public ServiceInterface getInterface() {
        return _serviceInterface;
    }

    @Override
    public QName getName() {
        return _serviceName;
    }

}
