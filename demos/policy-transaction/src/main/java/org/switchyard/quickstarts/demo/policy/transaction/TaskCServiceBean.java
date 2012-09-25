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

import javax.naming.InitialContext;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.switchyard.annotations.Requires;
import org.switchyard.component.bean.Service;
import org.switchyard.policy.TransactionPolicy;

/**
 *  Transactional service implementation. To trigger a rollback on the 
 *  current transaction, pass <code>TaskCServiceBean.ROLLBACK</code> as the command name.
 */
@Service(TaskCService.class)
@Requires(transaction = {TransactionPolicy.SUSPENDS_TRANSACTION,TransactionPolicy.NO_MANAGED_TRANSACTION})
public class TaskCServiceBean implements TaskCService {
    
    /** rollback command for this subtask. */
    public static final String ROLLBACK = WorkServiceBean.ROLLBACK + ".C";

    private static final String JNDI_TRANSACTION_MANAGER = "java:jboss/TransactionManager";
    
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
        } else {
            print("[Error] Managed transaction exists in spite of being marked as " + TransactionPolicy.NO_MANAGED_TRANSACTION);
        }
        
        if (command.contains(ROLLBACK)) {
            print(String.format("This service requires %s, so it never has transaction to rollback.", TransactionPolicy.NO_MANAGED_TRANSACTION));
        }
    }
    
    private Transaction getCurrentTransaction() throws Exception {
        TransactionManager tm = (TransactionManager)
                new InitialContext().lookup(JNDI_TRANSACTION_MANAGER);
        return tm.getTransaction();
    }
        
    private void print(String message) {
        System.out.println(":: TaskCService :: " + message);
    }
}
