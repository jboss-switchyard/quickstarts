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
 *  current transaction, pass <code>TaskAServiceBean.ROLLBACK</code> as the command name.
 */
@Service(TaskAService.class)
@Requires(transaction = {TransactionPolicy.PROPAGATES_TRANSACTION,TransactionPolicy.MANAGED_TRANSACTION_GLOBAL})
public class TaskAServiceBean implements TaskAService {
    
    /** rollback command for this subtask. */
    public static final String ROLLBACK = WorkServiceBean.ROLLBACK + ".A";

    private static final String JNDI_TRANSACTION_MANAGER = "java:jboss/TransactionManager";
    
    // counts the number of times a rollback has occurred
    private int _rollbackCounter = 0;
    
    @Inject @Reference("StoreAService") @Requires(transaction=TransactionPolicy.PROPAGATES_TRANSACTION)
    private StoreService _storeA;
    
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

        _storeA.store(command);
        
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
        System.out.println(":: TaskAService :: " + message);
    }
}
