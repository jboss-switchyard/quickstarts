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

import org.switchyard.internal.DomainImpl;
import org.switchyard.internal.EventManager;
import org.switchyard.internal.LocalExchangeBus;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.internal.validate.BaseValidatorRegistry;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.spi.ServiceRegistry;

public class MockDomain extends DomainImpl {
    
    public static final QName DEFAULT_DOMAIN = 
        new QName("urn:switchyard:test", "MockDomain");
    
    public MockDomain() {
        super(DEFAULT_DOMAIN);
    }
    
    public MockDomain(ServiceRegistry registry) {
        super(DEFAULT_DOMAIN,
                registry,
                new LocalExchangeBus(),
                new BaseTransformerRegistry(),
                new BaseValidatorRegistry(),
                new EventManager(),
                null);
    }
    
    public ServiceReference createInOnlyService(QName serviceName) {
        ServiceInterface inOnly = new InOnlyService();
        registerService(serviceName, inOnly, new MockHandler());
        ServiceReference reference = registerServiceReference(serviceName, inOnly);
        return reference;
    }
    

    public ServiceReference createInOnlyService(QName serviceName, ExchangeHandler handler) {
        ServiceInterface inOnly = new InOnlyService();
        registerService(serviceName, inOnly, handler);
        ServiceReference reference = registerServiceReference(serviceName, inOnly);
        return reference;
    }

    public ServiceReference createInOutService(QName serviceName) {
        ServiceInterface inOut = new InOutService();
        registerService(serviceName, inOut, new MockHandler());
        ServiceReference reference = registerServiceReference(serviceName, inOut);
        return reference;
    }

    public ServiceReference createInOutService(QName serviceName, ExchangeHandler handler) {
        ServiceInterface inOut = new InOutService();
        registerService(serviceName, inOut, handler);
        ServiceReference reference = registerServiceReference(serviceName, inOut);
        return reference;
    }
}
