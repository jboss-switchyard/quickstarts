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
package org.switchyard.test.mixins;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.switchyard.exception.SwitchYardException;

import com.arjuna.ats.jta.common.JTAEnvironmentBean;

/**
 * Mixin which bounds JTA beans in JNDI tree.
 * 
 * @author Lukasz Dywicki
 */
public class TransactionMixIn extends NamingMixIn {

    /**
     * Location of persistent store for tx logs.
     */
    private String storeDir = "target/tx-store";
	private JTAEnvironmentBean jtaEnvironmentBean;

    @Override
    public void initialize() {
        super.initialize();

        System.setProperty("ObjectStoreEnvironmentBean.objectStoreDir", storeDir);
        System.setProperty("com.arjuna.ats.arjuna.objectstore.objectStoreDir", storeDir);

        try {
            InitialContext initialContext = new InitialContext();
            jtaEnvironmentBean = new JTAEnvironmentBean();

            initialContext.bind("java:jboss/TransactionManager", jtaEnvironmentBean.getTransactionManager());
            initialContext.bind("java:jboss/UserTransaction", jtaEnvironmentBean.getUserTransaction());
            initialContext.bind("java:jboss/TransactionSynchronizationRegistry", jtaEnvironmentBean.getTransactionSynchronizationRegistry());
        } catch (NamingException e) {
            throw new SwitchYardException("Unable to bind transaction manager in JNDI", e);
        }
    }

    public UserTransaction getUserTransaction() {
        return jtaEnvironmentBean.getUserTransaction();
    }

    public TransactionManager getTransactionManager() {
       return jtaEnvironmentBean.getTransactionManager();
    }

    public TransactionSynchronizationRegistry getTransactionSynchronizationRegistry() {
        return jtaEnvironmentBean.getTransactionSynchronizationRegistry();
    }

}
