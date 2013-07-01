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
package org.switchyard.component.camel.common.handler;

import static org.mockito.Mockito.mock;

import java.net.URI;

import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;
import javax.xml.namespace.QName;

import org.apache.camel.component.mock.MockComponent;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.Mockito;
import org.springframework.transaction.PlatformTransactionManager;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.camel.common.CamelConstants;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.component.camel.common.transaction.TransactionManagerFactory;
import org.switchyard.component.test.mixins.naming.NamingMixIn;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;
import org.switchyard.config.model.selector.OperationSelectorModel;

/**
 * Base class for inbound handler tests.
 * 
 * All component-common-camel tests can not use switchyard component directly.
 * The camel mock component is used as replacement.
 * It depends on this module, thus its circular dependency. All tests related to
 * usage and execution of custom operation selector, message composer and context
 * mapper are place in camel-switchyard module.
 */
public abstract class InboundHandlerTestBase {

    private static NamingMixIn mixIn = new NamingMixIn();

    protected SwitchYardCamelContext _camelContext;
    protected Configuration _configuration;

    @Before
    public void startUp() {
        _camelContext = new SwitchYardCamelContext(false);
        _camelContext.addComponent("switchyard", new MockComponent());
        _configuration = mock(Configuration.class);
    }

    protected InboundHandler<?> createInboundHandler(final String uri) {
        return createInboundHandler(uri, null, null, null, null);
    }

    protected InboundHandler<?> createInboundHandler(final String uri, final String name, OperationSelectorModel selectorModel) {
        return createInboundHandler(uri, name, selectorModel, null, null);
    }

    protected InboundHandler<?> createInboundHandler(final String uri, final String name, MessageComposerModel composerModel) {
        return createInboundHandler(uri, name, null, composerModel, null);
    }

    protected InboundHandler<?> createInboundHandler(final String uri, final String name, ContextMapperModel mapperModel) {
        return createInboundHandler(uri, name, null, null, mapperModel);
    }

    protected InboundHandler<?> createInboundHandler(final String uri, final String name, final OperationSelectorModel selectorModel,
        final MessageComposerModel composerModel, final ContextMapperModel mapperModel) {
        V1BaseCamelBindingModel camelBindingModel = new V1BaseCamelBindingModel(_configuration, new Descriptor()) {
            @Override
            public String getName() {
                return name;
            }
            @Override
            public URI getComponentURI() {
                return URI.create(uri);
            }
            @Override
            public OperationSelectorModel getOperationSelector() {
                return selectorModel;
            }
            @Override
            public MessageComposerModel getMessageComposer() {
                return composerModel;
            }
            @Override
            public ContextMapperModel getContextMapper() {
                return mapperModel;
            }
        };
        return new InboundHandler<V1BaseCamelBindingModel>(camelBindingModel, _camelContext, new QName("urn:foo", "dummyService"), null);
    }

    protected void mockTransaction(String manager) {
        PlatformTransactionManager transactionManager = Mockito.mock(PlatformTransactionManager.class);
        _camelContext.getWritebleRegistry().put(manager, transactionManager);
        _camelContext.getWritebleRegistry().put(CamelConstants.TRANSACTED_REF, new SpringTransactionPolicy(transactionManager));
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        mixIn.initialize();
        mixIn.getInitialContext().bind(TransactionManagerFactory.JBOSS_USER_TRANSACTION, mock(UserTransaction.class));
        mixIn.getInitialContext().bind(TransactionManagerFactory.JBOSS_TRANSACTION_MANANGER, mock(TransactionManager.class));
        mixIn.getInitialContext().bind(TransactionManagerFactory.JBOSS_TRANSACTION_SYNC_REG, mock(TransactionSynchronizationRegistry.class));
    }

    @AfterClass
    public static void afterClass() {
        mixIn.uninitialize();
    }

}
