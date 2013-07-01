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
