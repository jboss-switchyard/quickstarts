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
package org.switchyard.component.test.mixins.transaction;

import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.weld.transaction.spi.TransactionServices;

import com.arjuna.ats.jta.common.JTAEnvironmentBean;

/**
 * Transaction services using arjuna to manage transaction.
 */
public class LocalArjunaTransactionServices implements TransactionServices {

    private final JTAEnvironmentBean _jtaEnvironmentBean;

    /**
     * Creates new local transaction services.
     * 
     * @param jtaEnvironmentBean Arjuna jta bean. 
     */
    public LocalArjunaTransactionServices(JTAEnvironmentBean jtaEnvironmentBean) {
        this._jtaEnvironmentBean = jtaEnvironmentBean;
    }

    @Override
    public void registerSynchronization(Synchronization synchronizedObserver) {
        _jtaEnvironmentBean.getTransactionSynchronizationRegistry()
            .registerInterposedSynchronization(synchronizedObserver);
    }

    @Override
    public UserTransaction getUserTransaction() {
        return _jtaEnvironmentBean.getUserTransaction();
    }

    @Override
    public boolean isTransactionActive() {
        try {
            final int status = getUserTransaction().getStatus();
            return status == Status.STATUS_ACTIVE
                || status == Status.STATUS_COMMITTING 
                || status == Status.STATUS_MARKED_ROLLBACK 
                || status == Status.STATUS_PREPARED 
                || status == Status.STATUS_PREPARING 
                || status == Status.STATUS_ROLLING_BACK;
        } catch (SystemException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanup() {
    }

    @Override
    public String toString() {
        return "Local Arjuna Transaction";
    }
}
