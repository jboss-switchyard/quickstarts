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
package org.switchyard.component.test.mixins.transaction;

import com.arjuna.ats.jta.common.JTAEnvironmentBean;

/**
 * Helper interface which lets abstract {@link JTAEnvironmentBean} lookup.
 */
public interface TransactionMixInParticipant {

    /**
     * Locates Arjuna environment bean, if null value is returned then
     * TransactionMixIn will continue looking it in next participant. If all
     * participants will return null value then default JTAEnvironmentBean will
     * be used.
     * 
     * @return Instance of environment bean or null.
     * @throws Throwable In case of failure to look up.
     */
    JTAEnvironmentBean locateEnvironmentBean() throws Throwable;

}
