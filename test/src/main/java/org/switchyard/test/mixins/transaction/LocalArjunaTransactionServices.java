/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.switchyard.test.mixins.transaction;

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
