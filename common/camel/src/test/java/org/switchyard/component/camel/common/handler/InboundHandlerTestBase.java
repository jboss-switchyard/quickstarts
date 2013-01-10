/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.camel.common.handler;

import static org.mockito.Mockito.mock;

import java.net.URI;

import javax.xml.namespace.QName;

import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.transaction.PlatformTransactionManager;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.camel.common.CamelConstants;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.selector.OperationSelectorModel;

/**
 * Base class for inbound handler tests.
 */
public abstract class InboundHandlerTestBase {

    protected SwitchYardCamelContext _camelContext;
    protected Configuration _configuration;

    @Before
    public void startUp() {
        _camelContext = new SwitchYardCamelContext(false);
        _configuration = mock(Configuration.class);
    }

    protected InboundHandler<?> createInboundHandler(final String uri) {
        return createInboundHandler(uri, null);
    }

    protected InboundHandler<?> createInboundHandler(final String uri, final OperationSelectorModel selectorModel) {
        final V1BaseCamelBindingModel camelBindingModel = new V1BaseCamelBindingModel(_configuration, new Descriptor()) {
            @Override
            public URI getComponentURI() {
                return URI.create(uri);
            }
            @Override
            public OperationSelectorModel getOperationSelector() {
                return selectorModel;
            }
        };
        return new InboundHandler<V1BaseCamelBindingModel>(camelBindingModel, _camelContext, new QName("urn:foo", "dummyService"));
    }

    protected void mockTransaction(String manager) {
        PlatformTransactionManager transactionManager = Mockito.mock(PlatformTransactionManager.class);
        _camelContext.getWritebleRegistry().put(manager, transactionManager);
        _camelContext.getWritebleRegistry().put(CamelConstants.TRANSACTED_REF, new SpringTransactionPolicy(transactionManager));
    }
}
