/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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

/**
 * Interface providing location services for transaction managers.
 */
public abstract class TransactionManagerLocator {
    
    /** The locator instance. */
    public static final TransactionManagerLocator INSTANCE;

    /**
     * Get the TransactionManager, if possible.
     * 
     * @return the TransactionManager
     */
    public abstract TransactionManager getTransactionManager();

    /**
     * Get the UserTransaction, if possible.
     * 
     * @return the UserTransaction
     */
    public abstract UserTransaction getUserTransaction();
    
    static {
        TransactionManagerLocator instance = new TransactionManagerLocator() {
            
            @Override
            public UserTransaction getUserTransaction() {
                return AS7TransactionHelper.getUserTransaction();
            }
            
            @Override
            public TransactionManager getTransactionManager() {
                return AS7TransactionHelper.getTransactionManager();
            }
        };
        try {
            TransactionManagerLocator.class.getClassLoader().loadClass("org.osgi.framework.Bundle");
            instance = new OsgiTransactionManagerLocator();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        INSTANCE = instance;
    }
}
