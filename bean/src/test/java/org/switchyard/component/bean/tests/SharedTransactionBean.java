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

package org.switchyard.component.bean.tests;

import javax.inject.Inject;

import org.switchyard.annotations.Requires;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;
import org.switchyard.policy.TransactionPolicy;

@Requires(transaction = TransactionPolicy.PROPAGATES_TRANSACTION)
@Service(value = OneWay.class, name = "SharedTransactionService")
public class SharedTransactionBean implements OneWay {
    
    @Inject @Reference @Requires(transaction = TransactionPolicy.PROPAGATES_TRANSACTION)
    private OneWay oneWay;

	@Override
	public void oneWay(Object message) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void oneWayNoArgs() {
        // TODO Auto-generated method stub
        
    }
    
}
