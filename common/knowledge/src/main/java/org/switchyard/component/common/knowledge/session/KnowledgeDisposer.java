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
package org.switchyard.component.common.knowledge.session;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Knowledge disposer.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class KnowledgeDisposer implements KnowledgeDisposal {

    private static Logger LOGGER = Logger.getLogger(KnowledgeDisposer.class);

    private final Set<KnowledgeDisposal> _disposals = Collections.synchronizedSet(new LinkedHashSet<KnowledgeDisposal>());

    /**
     * Adds disposals.
     * @param disposals disposals
     */
    public synchronized void addDisposals(KnowledgeDisposal... disposals) {
        if (disposals != null) {
            for (KnowledgeDisposal disposal : disposals) {
                if (disposal != null && disposal != this) {
                    _disposals.add(disposal);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void dispose() {
        for (KnowledgeDisposal disposal : _disposals) {
            try {
                disposal.dispose();
            } catch (Throwable t) {
                LOGGER.warn(String.format("problem disposing [%s]: %s", disposal.getClass().getSimpleName(), t.getMessage()));
            }
        }
        _disposals.clear();
    }

}
