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
package org.switchyard.handlers;

import java.util.Iterator;
import java.util.Set;

import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.policy.Policy;
import org.switchyard.policy.PolicyUtil;
import org.switchyard.runtime.RuntimeMessages;

/**
 * Generic policy handler which simply checks to make sure all required policies
 * have been provided.  This handler should always occur *after* the other
 * policy handlers in the handler chain, which allows policy-specific handlers
 * to adjust the set of provided/required policies as appropriate.
 */
public class PolicyHandler extends BaseHandler {

    /**
     * Create a new PolicyHandler.
     */
    public PolicyHandler() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        // only execute on the IN phase
        if (ExchangePhase.IN.equals(exchange.getPhase())) {
            Set<Policy> required = PolicyUtil.getRequired(exchange);
            Iterator<Policy> reqIt = required.iterator();
            while (reqIt.hasNext()) {
                if (PolicyUtil.isProvided(exchange, reqIt.next())) {
                    reqIt.remove();
                }
            }
            if (!required.isEmpty()) {
                // Required policies are missing.  Format the list for fault message.
                Iterator<Policy> missing = required.iterator();
                StringBuilder requires = new StringBuilder(missing.next().getName());
                while (missing.hasNext()) {
                    requires.append(" " + missing.next().getName());
                }
                throw RuntimeMessages.MESSAGES.requiredPolicesNeeded(requires.toString());
            }
        }
    }

}
