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
import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;
import org.switchyard.runtime.util.TransactionManagerLocator;

/**
 *  Transactional service implementation. To trigger a rollback on the 
 *  current transaction, pass <code>WorkService.ROLLBACK</code> as the command name.
 */
@Service(WorkService.class)
public class WorkServiceBean
    implements org.switchyard.quickstarts.demo.policy.transaction.WorkService {

    /** rollback command. */
    public static final String ROLLBACK = "rollback";

    @Inject
    @Reference
    private TaskAService _taskAService;

    @Inject
    @Reference
    private TaskBService _taskBService;

    @Inject
    @Reference
    private TaskCService _taskCService;

    @Override
    public final void doWork(final String command) {

        print("Received command =>  " + command);

        Transaction t = null;
        try {
            t = getCurrentTransaction();
        } catch (Exception e) {
            print("Failed to get current transcation");
            return;
        }
        if (t == null) {
            print("No active transaction");
        }
        _taskAService.doTask(command);
        _taskBService.doTask(command);
        _taskCService.doTask(command);

        try {
            t = getCurrentTransaction();
            if (t == null) {
                print("No active transaction");
                return;
            } else if (t.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
                print("transaction is marked as rollback only");
            } else if (t.getStatus() == Status.STATUS_ACTIVE) {
                print("transaction will be committed");
            } else {
                print("Invalid transaction status: " + t.getStatus());
            }
        } catch (Exception e) {
            print("Failed to get current transaction status");
        }
    }

    private Transaction getCurrentTransaction() throws Exception {
        TransactionManager tm = TransactionManagerLocator.locateTransactionManager();
        return tm.getTransaction();
    }

    private void print(String message) {
        System.out.println(":: WorkService :: " + message);
    }
}
