/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.config.model.remote;

/**
 * Camel remote file consumer binding. 
 * 
 * @author Lukasz Dywicki
 */
public interface CamelRemoteFileConsumerBindingModel {

    /**
     * If a directory, will look for changes in files in all the sub-directories.
     * 
     * @return Recursive mode.
     */
    Boolean getRecursive();

    /**
     * Sets recursive mode.
     * 
     * @param recursive Recursive mode.
     * @return a reference to this binding model
     */
    CamelRemoteFileConsumerBindingModel setRecursive(Boolean recursive);

    /**
     * Delay in milliseconds between each poll.
     * 
     * @return Delay.
     */
    Integer getDelay();

    /**
     * Sets delay between each poll.
     * 
     * @param delay Delay.
     * @return a reference to this binding model
     */
    CamelRemoteFileConsumerBindingModel setDelay(Integer delay);

    /**
     * Milliseconds before polling starts.
     * 
     * @return Initial delay.
     */
    Integer getInitialDelay();

    /**
     * Sets initial delay.
     * 
     * @param initialDelay Initial delay.
     * @return a reference to this binding model
     */
    CamelRemoteFileConsumerBindingModel setInitialDelay(Integer initialDelay);

    /**
     * Returns actual value of fixed delay.
     * 
     * @return Use fixed delay.
     */
    Boolean getUseFixedDelay();

    /**
     * Set to true to use fixed delay between polls, otherwise fixed rate is used.
     * See ScheduledExecutorService in JDK for details.
     * 
     * @param useFixedDelay Use fixed delay.
     * @return a reference to this binding model
     */
    CamelRemoteFileConsumerBindingModel setUseFixedDelay(Boolean useFixedDelay);

}
