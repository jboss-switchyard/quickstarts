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
package org.switchyard.component.test.mixins.naming;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.as.naming.InMemoryNamingStore;
import org.jboss.as.naming.NamingContext;
import org.jboss.as.naming.context.NamespaceContextSelector;
import org.junit.Assert;
import org.switchyard.test.mixins.AbstractTestMixIn;

/**
 * Code related to JNDI stuff.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 * @author Lukasz Dywicki
 */
public class NamingMixIn extends AbstractTestMixIn {

    private static final String INITIAL_CONTEXT_FACTORY_NAME = "org.jboss.as.naming.InitialContextFactory";
    private static final CompositeName EMPTY_NAME = new CompositeName();
    
    /**
     * Instance of context shared with children classes.
     */
    private static InitialContext initialContext;

    @Override
    public void initialize() {
        String factoryName = System.getProperty(Context.INITIAL_CONTEXT_FACTORY);
        if (factoryName != null && !factoryName.equals(INITIAL_CONTEXT_FACTORY_NAME)) {
            return;
        }

        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY_NAME);
        NamingContext.initializeNamingManager();
        NamespaceContextSelector.setDefault(new NamespaceContextSelector() {
            public Context getContext(String identifier) {
                try {
                    return (Context) new InitialContext().lookup(EMPTY_NAME);
                } catch (NamingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        if (initialContext == null) {
            try {
                initialContext = new InitialContext();
                try {
                    Context.class.cast(initialContext.lookup("java:comp"));
                } catch (Exception e) {
                    initialContext.createSubcontext("java:comp");
                }
            } catch (NamingException e) {
                Assert.fail("Failed to create context : " + e.getMessage());
            }
        }
    }

    /**
     * Returns the InitialContext used in the NamingMixIn.
     * @return InitialContext instance
     */
    public InitialContext getInitialContext() {
        return initialContext;
    }

    @Override
    public void uninitialize() {
        NamingContext.setActiveNamingStore(new InMemoryNamingStore());
    }
}
