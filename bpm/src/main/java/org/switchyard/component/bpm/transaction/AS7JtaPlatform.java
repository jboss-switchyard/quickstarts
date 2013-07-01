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

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.service.jta.platform.internal.AbstractJtaPlatform;

/**
 * AS7JtaPlatform.
 * 
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@SuppressWarnings("serial")
public class AS7JtaPlatform extends AbstractJtaPlatform {

    /**
     * {@inheritDoc}
     */
    @Override
    protected TransactionManager locateTransactionManager() {
        return (TransactionManager)AS7TransactionHelper.getTransactionManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected UserTransaction locateUserTransaction() {
        return (UserTransaction)AS7TransactionHelper.getUserTransaction();
    }

}
