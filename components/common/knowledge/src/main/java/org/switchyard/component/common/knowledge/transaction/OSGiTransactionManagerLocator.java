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
package org.switchyard.component.common.knowledge.transaction;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * TransactionManagerLocator for OSGi environments.
 */
public class OSGiTransactionManagerLocator extends TransactionManagerLocator {

    private TransactionManager _transactionManager;
    private UserTransaction _userTransaction;

    /**
     * Create a new OsgiTransactionManagerLocator.
     */
    public OSGiTransactionManagerLocator() {
        final Bundle bundle = FrameworkUtil.getBundle(getClass());
        final BundleContext context = bundle.getBundleContext();
        _transactionManager = context.getService(context.getServiceReference(TransactionManager.class));
        _userTransaction = context.getService(context.getServiceReference(UserTransaction.class));
    }

    @Override
    public TransactionManager getTransactionManager() {
        return _transactionManager;
    }

    @Override
    public UserTransaction getUserTransaction() {
        return _userTransaction;
    }

}
