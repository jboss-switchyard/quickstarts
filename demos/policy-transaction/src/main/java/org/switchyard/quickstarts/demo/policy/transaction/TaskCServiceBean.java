/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;
import org.switchyard.policy.TransactionPolicy;
import org.switchyard.runtime.util.TransactionManagerLocator;

/**
 *  Transactional service implementation. To trigger a rollback on the 
 *  current transaction, pass <code>TaskCServiceBean.ROLLBACK</code> as the command name.
 */
@Service(TaskCService.class)
public class TaskCServiceBean implements TaskCService {

    /** rollback command for this subtask. */
    public static final String ROLLBACK = WorkServiceBean.ROLLBACK + ".C";

    @Inject
    @Reference("StoreCService")
    private StoreService _storeC;

    @Override
    public final void doTask(final String command) {

        print("Received command =>  " + command);
        Transaction t = null;
        try {
            t = getCurrentTransaction();
        } catch (Exception e) {
            print("Failed to get current transcation");
        }

        _storeC.store(command);

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
        TransactionManager tm = TransactionManagerLocator.locateTransactionManager();
        return tm.getTransaction();
    }

    private void print(String message) {
        System.out.println(":: TaskCService :: " + message);
    }
}
