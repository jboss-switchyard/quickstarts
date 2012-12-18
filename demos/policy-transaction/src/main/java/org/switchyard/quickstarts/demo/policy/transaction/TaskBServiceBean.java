/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010-2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License, v.2.1 along with this distribution; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.switchyard.quickstarts.demo.policy.transaction;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.switchyard.annotations.Requires;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;
import org.switchyard.policy.TransactionPolicy;

/**
 *  Transactional service implementation. To trigger a rollback on the 
 *  current transaction, pass <code>TaskBServiceBean.ROLLBACK</code> as the command name.
 */
@Service(TaskBService.class)
@Requires(transaction = {TransactionPolicy.SUSPENDS_TRANSACTION,TransactionPolicy.MANAGED_TRANSACTION_LOCAL})
public class TaskBServiceBean implements TaskBService {
    
    /** rollback command for this subtask. */
    public static final String ROLLBACK = WorkServiceBean.ROLLBACK + ".B";

    private static final String JNDI_TRANSACTION_MANAGER = "java:jboss/TransactionManager";
    
    // counts the number of times a rollback has occurred
    private int _rollbackCounter = 0;
    
    @Inject @Reference("StoreBService") @Requires(transaction=TransactionPolicy.SUSPENDS_TRANSACTION)
    private StoreService _storeB;
    
    @Override
    public final void doTask(final String command) {
        
        print("Received command =>  " + command);
        Transaction t = null;
        try {
            t = getCurrentTransaction();
        } catch (Exception e) {
            print("Failed to get current transcation");
        }
        if (t == null) {
            print("No active transaction");
            return;
        }

        _storeB.store(command);
        
        if (command.contains(ROLLBACK)) {
            try {
                if (++_rollbackCounter % 4 != 0) {
                    t.setRollbackOnly();
                    print("Marked transaction to rollback!");
                } else {
                    print("Rollbacks completed - will be committed");
                }
            } catch (Exception ex) {
                print("Failed to rollback transaction: " + ex.toString());
            }
        }
    }
    
    private Transaction getCurrentTransaction() throws Exception {
        TransactionManager tm = (TransactionManager)
                new InitialContext().lookup(JNDI_TRANSACTION_MANAGER);
        return tm.getTransaction();
    }
        
    private void print(String message) {
        System.out.println(":: TaskBService :: " + message);
    }
}
