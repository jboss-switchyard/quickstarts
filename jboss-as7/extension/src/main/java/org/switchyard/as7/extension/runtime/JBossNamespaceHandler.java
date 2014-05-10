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
package org.switchyard.as7.extension.runtime;

import java.util.EventObject;

import org.jboss.as.naming.context.NamespaceContextSelector;
import org.switchyard.event.EventObserver;
import org.switchyard.runtime.event.ExchangeCompletionEvent;
import org.switchyard.runtime.event.ExchangeInitiatedEvent;

/**
 * The NamespaceHandler initializes a JNDI context selector.
 */
public class JBossNamespaceHandler implements EventObserver {

    private NamespaceContextSelector _selector;

    /**
     * Create a new NamespaceHandler for the specified domain's namespace selector.
     * @param selector Namespace selector for this domain
     */
    public JBossNamespaceHandler(NamespaceContextSelector selector) {
        _selector = selector;
    }

    public void notify(EventObject event) {
        if (event instanceof ExchangeInitiatedEvent) {
            NamespaceContextSelector.pushCurrentSelector(_selector);
        } else if (event instanceof ExchangeCompletionEvent) {
            NamespaceContextSelector.popCurrentSelector();
        }
    }
}
