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
package org.switchyard.test.mixins;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.as.naming.InMemoryNamingStore;
import org.jboss.as.naming.NamingContext;
import org.jboss.as.naming.context.NamespaceContextSelector;
import org.junit.Assert;

/**
 * Code related to JNDI stuff.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 * @author Lukasz Dywicki
 */
public class NamingMixIn extends AbstractTestMixIn {

    private static final String INITIAL_CONTEXT_FACTORY_NAME = "org.jboss.as.naming.InitialContextFactory";
    private static final CompositeName EMPTY_NAME = new CompositeName();
    private static Integer referenceCounter = 0;
    
    /**
     * Instance of context shared with children classes.
     */
    private static InitialContext initialContext;

    @Override
    public void initialize() {
        // ensure to initialize only once and count references
        // TODO remove this once SWITCHYARD-906 is fixed
        synchronized (referenceCounter) {
            if (referenceCounter != 0) {
                referenceCounter++;
                return;
            }

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
            referenceCounter++;
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
        // ensure to uninitialize after all of mixins depend on NamingMixIn have been uninitialized
        // TODO remove this once SWITCHYARD-906 is fixed
        synchronized (referenceCounter) {
            referenceCounter--;
            if (referenceCounter == 0) {
                NamingContext.setActiveNamingStore(new InMemoryNamingStore());
            }
        }
    }
}
