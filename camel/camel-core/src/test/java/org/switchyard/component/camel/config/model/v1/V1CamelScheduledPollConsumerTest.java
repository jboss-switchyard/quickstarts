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

import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.switchyard.component.camel.config.model.CamelScheduledPollConsumer;

/**
 * Test of scheduled poll consumer binding.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelScheduledPollConsumerTest {

    private static final Integer DELAY = 1000;
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;
    private static final Boolean SEND_EMPTY_MESSAGE_WHEN_IDLE = true;
    private static final Integer INITIAL_DELAY = 999;
    private static final Boolean USE_FIXED_DELAY = true;

    @Test
    public void testConfigOverride() {
        CamelScheduledPollConsumer model = createModel();
        assertEquals(DELAY, model.getDelay());
        assertEquals(INITIAL_DELAY, model.getInitialDelay());
        assertEquals(TIME_UNIT, model.getTimeUnit());
        assertEquals(SEND_EMPTY_MESSAGE_WHEN_IDLE, model.isSendEmptyMessageWhenIdle());
        assertEquals(USE_FIXED_DELAY, model.isUseFixedDelay());
        model.setDelay(750);
        assertEquals(new Integer(750), model.getDelay());
    }

    private CamelScheduledPollConsumer createModel() {
        return new V1CamelScheduledPollConsumer(new QName("test"))
            .setDelay(DELAY)
            .setInitialDelay(INITIAL_DELAY)
            .setTimeUnit(TIME_UNIT.name())
            .setSendEmptyMessageWhenIdle(SEND_EMPTY_MESSAGE_WHEN_IDLE)
            .setUseFixedDelay(USE_FIXED_DELAY);
    }
}
