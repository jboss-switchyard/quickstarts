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
package org.switchyard.component.camel.config.model;

import java.util.concurrent.TimeUnit;

/**
 * Scheduled poll consumer interface. Defines common properties for schedule
 * based consumers.
 * 
 * @author Lukasz Dywicki
 */
public interface CamelScheduledPollConsumer {

    /**
     * Delay before the next poll.
     * 
     * @return the delay setting or null if it has not been specified.
     */
    Integer getDelay();

    /**
     * Specify the time before next poll.
     * 
     * @param delay the time in configured time unit.
     * @return a reference to this binding model.
     */
    CamelScheduledPollConsumer setDelay(Integer delay);

    /**
     * Delay before polling starts in given time unit.
     * 
     * @return the initial delay setting or null if it has not been specified.
     */
    Integer getInitialDelay();

    /**
     * Specify the time before polling starts.
     * 
     * @param initialDelay the time in configured time unit.
     * @return a reference to this binding model.
     */
    CamelScheduledPollConsumer setInitialDelay(Integer initialDelay);

    /**
     * Time unit used to count delay and initial delay. By default it is millisecond.
     * 
     * @return Time unit of initial delay/delay.
     */
    TimeUnit getTimeUnit();

    /**
     * Specify the time unit of initial delay/delay parameters.
     * 
     * @param timeUnit Time unit to use.
     * @return a reference to this binding model
     */
    CamelScheduledPollConsumer setTimeUnit(String timeUnit);

    /**
     * Controls if fixed delay or fixed rate is used. 
     * 
     * @return True if fixed rate is used.
     */
    Boolean isUseFixedDelay();

    /**
     * Specify whether to use fixed delay between pools, otherwise fixed rate is
     * used.
     * 
     * @param useFixedDelay true: fixed delay between pools. False: fixed rate is used
     * @return a reference to this binding model.
     */
    CamelScheduledPollConsumer setUseFixedDelay(Boolean useFixedDelay);

    /**
     * If the polling consumer did not poll any message, you can enable this option
     * to send an empty message (no body) instead.
     * 
     * @return Should empty message be sent when there is nothing to poll.
     */
    Boolean isSendEmptyMessageWhenIdle();

    /**
     * Specify whether to use empty messages if there is nothing to poll.
     * 
     * @param sendEmptyMessageWhenIdle True - empty message will be sent.
     * @return a reference to this binding model.
     */
    CamelScheduledPollConsumer setSendEmptyMessageWhenIdle(Boolean sendEmptyMessageWhenIdle);

}
