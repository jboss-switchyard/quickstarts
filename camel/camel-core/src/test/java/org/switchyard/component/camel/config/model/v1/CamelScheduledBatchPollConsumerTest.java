/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.camel.config.model.v1;

import static junit.framework.Assert.assertEquals;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.switchyard.component.camel.config.model.CamelScheduledBatchPollConsumer;

/**
 * Test of scheduled batch poll consumer binding.
 * 
 * @author Lukasz Dywicki
 */
public class CamelScheduledBatchPollConsumerTest {

    private static final Integer MAX_MESSAGES_PER_POLL = 5;

    @Test
    public void testConfigOverride() {
        CamelScheduledBatchPollConsumer model = createModel();
        assertEquals(MAX_MESSAGES_PER_POLL, model.getMaxMessagesPerPoll());
    }

    private CamelScheduledBatchPollConsumer createModel() {
        return new V1CamelScheduledBatchPollConsumer(new QName("test"))
            .setMaxMessagesPerPoll(MAX_MESSAGES_PER_POLL);
    }
}
