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
package org.switchyard.component.test.mixins.jca;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;

/**
 * A TransactionManager locator which is used by HornetQ ResourceAdapter.
 * The way to get TransactionManager from actual JBossAS instance doesn't work
 * with IronJacamar embedded, so this class performs that instead.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class TransactionManagerLocator {
    /**
     * get TransactionManager.
     * 
     * @return TransactionManager
     * @throws Exception 
     */
    public TransactionManager getTransactionManager() throws Exception {
        return (TransactionManager) new InitialContext().lookup("java:jboss/TransactionManager");
    }
}
