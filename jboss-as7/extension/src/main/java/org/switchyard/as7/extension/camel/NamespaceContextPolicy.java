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
package org.switchyard.as7.extension.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.impl.RoutePolicySupport;
import org.jboss.as.naming.context.NamespaceContextSelector;

/**
 * A RoutePolicy implementation which pushes the namespace context at the
 * beginning of an exchange and pops it at the completion of the exchange. These
 * callbacks are invoked on happy and failure paths.
 */
public class NamespaceContextPolicy extends RoutePolicySupport {

    /**
     * The name this policy uses when stored in the Camel registry.
     */
    public static final String POLICY_REF = "org.switchyard.namespaceContextPolicy";

    private NamespaceContextSelector _selector;

    /**
     * Create a new NamespaceContextPolicy.
     * @param selector the context selector to use.
     */
    public NamespaceContextPolicy(NamespaceContextSelector selector) {
        _selector = selector;
    }

    @Override
    public void onExchangeBegin(Route route, Exchange exchange) {
        NamespaceContextSelector.pushCurrentSelector(_selector);
    }

    @Override
    public void onExchangeDone(Route route, Exchange exchange) {
        // guard against an empty namespace stack
        if (NamespaceContextSelector.getCurrentSelector() != null) {
            NamespaceContextSelector.popCurrentSelector();
        }
    }
}
