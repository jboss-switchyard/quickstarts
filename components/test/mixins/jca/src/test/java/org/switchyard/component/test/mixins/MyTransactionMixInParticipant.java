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
package org.switchyard.component.test.mixins;

import java.util.concurrent.Callable;

import org.switchyard.component.test.mixins.transaction.TransactionMixInParticipant;
import org.switchyard.test.mixins.AbstractTestMixIn;

import com.arjuna.ats.jta.common.JTAEnvironmentBean;

/**
 * A mock TransactionMixInParticipant.
 */
class MyTransactionMixInParticipant extends AbstractTestMixIn implements TransactionMixInParticipant {
    private Callable<Object> callback;
    public MyTransactionMixInParticipant(Callable<Object> callback) {
        this.callback = callback;
    }

    @Override
    public JTAEnvironmentBean locateEnvironmentBean() throws Throwable {
        callback.call();
        return null;
    }
    
}