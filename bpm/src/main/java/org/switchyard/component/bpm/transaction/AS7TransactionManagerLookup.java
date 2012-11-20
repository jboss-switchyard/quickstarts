/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.bpm.transaction;

import java.util.Properties;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.hibernate.HibernateException;
import org.hibernate.transaction.TransactionManagerLookup;

/**
 * AS7TransactionManagerLookup.
 * <br/><br/>
 * See: <a href="http://kverlaen.blogspot.com/2011/07/jbpm5-on-as7-lightning.html">jBPM5 on AS7: Lightning !</a>
 * @deprecated use AS7JtaPlatform instead
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@Deprecated
public class AS7TransactionManagerLookup implements TransactionManagerLookup {

    /**
     * {@inheritDoc}
     * @Override
     */
    public TransactionManager getTransactionManager(Properties properties) throws HibernateException {
        return (TransactionManager)AS7TransactionHelper.getTransactionManager(properties);
    }

    /**
     * {@inheritDoc}
     * @Override
     */
    public String getUserTransactionName() {
        return AS7TransactionHelper.JNDI_USER_TRANSACTION;
    }

    /**
     * {@inheritDoc}
     * @Override
     */
    public Object getTransactionIdentifier(Transaction transaction) {
        return transaction;
    }

}
