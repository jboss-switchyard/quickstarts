/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.component.camel.common.model.v1;

import org.switchyard.component.camel.common.model.consumer.CamelScheduledBatchPollConsumer;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Implementation of 1st batching poll consumer interface.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelScheduledBatchPollConsumer extends V1CamelScheduledPollConsumer 
    implements CamelScheduledBatchPollConsumer {

    /**
     * The name of the 'maxMessagesPerPoll' element.
     */
    private static final String MAX_MESSAGES_PER_POLL = "maxMessagesPerPoll";

    /**
     * Creates model bound to given namespace.
     * 
     * @param name Name of element.
     * @param namespace Namespace to bound.
     */
    public V1CamelScheduledBatchPollConsumer(String name, String namespace) {
        super(name, namespace);

        setModelChildrenOrder(MAX_MESSAGES_PER_POLL);
    }

    /**
     * Constructor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelScheduledBatchPollConsumer(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public Integer getMaxMessagesPerPoll() {
        return getIntegerConfig(MAX_MESSAGES_PER_POLL);
    }

    @Override
    public V1CamelScheduledBatchPollConsumer setMaxMessagesPerPoll(Integer maxMessagesPerPoll) {
        return setConfig(MAX_MESSAGES_PER_POLL, maxMessagesPerPoll);
    }

}
