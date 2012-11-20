/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.switchyard.component.common.knowledge.session;

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

    private final Set<KnowledgeDisposal> _disposals = new LinkedHashSet<KnowledgeDisposal>();

    /**
     * Adds disposals.
     * @param disposals disposals
     */
    public void addDisposals(KnowledgeDisposal... disposals) {
        if (disposals != null) {
            for (KnowledgeDisposal disposal : disposals) {
                if (disposal != null) {
                    _disposals.add(disposal);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
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
