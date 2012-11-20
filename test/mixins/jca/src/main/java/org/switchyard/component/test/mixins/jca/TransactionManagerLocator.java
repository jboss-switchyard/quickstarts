/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.component.test.mixins.jca;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;

/**
 * A TransactionManager locator which is used by HornetQ ResourceAdapter.
 * The way to get TransactionManager from actual JBossAS instance doesn't work
 * with IronJacamar embedded, so this class performs that instead.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class TransactionManagerLocator {
    /**
     * get TransactionManager.
     * 
     * @return TransactionManager
     * @throws Exception 
     */
    public TransactionManager getTransactionManager() throws Exception {
        return (TransactionManager) new InitialContext().lookup("java:jboss/TransactionManager");
    }
}
