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

package org.switchyard.component.camel.config.model.seda;

import org.switchyard.component.camel.config.model.CamelBindingModel;

/**
 * Represents the configuration settings for a SEDA endpoint in Camel. 
 * 
 * The SEDA component provides asynchronous SEDA behavior, so that messages 
 * are exchanged on a BlockingQueue and consumers are invoked in a separate 
 * thread from the producer. 
 * Note that queues are only visible within a single CamelContext. If you 
 * want to communicate across CamelContext instances (for example, 
 * communicating between Web applications), see the VM component. This 
 * component does not implement any kind of persistence or recovery, if 
 * the VM terminates while messages are yet to be processed. If you need 
 * persistence, reliability or distributed SEDA, try using either JMS 
 * or ActiveMQ.
 * 
 * @author Mario Antollini
 * 
 */
public interface CamelSedaBindingModel extends CamelBindingModel {
    
    /**
     * The name that uniquely identifies the endpoint within the 
     * current CamelContext.
     * @return The name that uniquely identifies the endpoint within the 
     * current CamelContext
     */
    String getName();

    /**
     * The name that uniquely identifies the endpoint within the 
     * current CamelContext.
     * @param name the name that uniquely identifies the endpoint within the 
     * current CamelContext
     * @return a reference to this SEDA binding model
     */
    CamelSedaBindingModel setName(String name);

    /**
     * The maximum size (= capacity of the number of messages it can max hold) 
     * of the SEDA queue. 
     * @return the maximum size of the SEDA queue 
     */
    Integer getSize();
    
    /**
     * Specify the maximum size (= capacity of the number of messages it can 
     * max hold) of the SEDA queue.
     * @param size the number of messages it can max hold
     * @return a reference to this SEDA binding model
     */
    CamelSedaBindingModel setSize(Integer size);
    
    /**
     * Number of concurrent threads processing exchanges. 
     * @return the number of concurrent threads processing exchanges.
     */
    Integer getConcurrentConsumers();
    
    /**
     * Specify the number of concurrent threads processing exchanges. 
     * @param concurrentConsumers the number of concurrent threads
     * @return a reference to this SEDA binding model
     */
    CamelSedaBindingModel setConcurrentConsumers(Integer concurrentConsumers);
    
    /**
     * Whether the caller should wait for the async task to complete or not 
     * before continuing. 
     * @return one of the following: Always, Never or IfReplyExpected. The 
     * first two values are self-explanatory. The last value, IfReplyExpected, 
     * will only wait if the message is Request Reply based.
     */
    String getWaitForTaskToComplete();
    
    /**
     * Specify whether the caller should wait for the async task to complete or not 
     * before continuing. The following three options are supported: Always, 
     * Never or IfReplyExpected. 
     * The first two values are self-explanatory. The last value, IfReplyExpected, 
     * will only wait if the message is Request Reply based. 
     * The default option is IfReplyExpected.
     * @param waitForTaskToComplete The following three options are supported: Always, 
     * Never or IfReplyExpected
     * @return a reference to this SEDA binding model
     */
    CamelSedaBindingModel setWaitForTaskToComplete(String waitForTaskToComplete);
    
    /**
     * Timeout in milliseconds a SEDA producer will at most waiting for an async 
     * task to complete. 
     * @return the time in milliseconds
     */
    Long getTimeout();
    
    /**
     * Timeout in milliseconds a SEDA producer will at most waiting for an async 
     * task to complete.
     * @param timeout the timeout in milliseconds
     * @return a reference to this SEDA binding model
     */
    CamelSedaBindingModel setTimeout(Long timeout);
    
    /**
     * Whether multiple consumers is allowed or not.
     * @return true if multiple consumers is allowed; false otherwise
     */
    Boolean isMultipleConsumers();
    
    /**
     * Specify whether multiple consumers is allowed or not. If enabled you can 
     * use SEDA for a pubsub kinda style messaging. Send a message to a SEDA queue and 
     * have multiple consumers receive a copy of the message.
     * @param multipleConsumers true if multiple consumers is allowed; false 
     * otherwise
     * @return a reference to this SEDA binding model
     */
    CamelSedaBindingModel setMultipleConsumers(Boolean multipleConsumers);
    
    /**
     * Whether to limit the concurrentConsumers to maximum 500. 
     * If its configured with a higher number an exception will be thrown. 
     * @return true if the limit is enabled; false if the limit is disabled
     */
    Boolean isLimitConcurrentConsumers();
    
    /**
     * Whether to limit the concurrentConsumers to maximum 500. 
     * If its configured with a higher number an exception will be thrown.
     * @param limitConcurrentConsumers true to limit to 500; false otherwise
     * @return a reference to this SEDA binding model
     */
    CamelSedaBindingModel setLimitConcurrentConsumers(Boolean limitConcurrentConsumers);
}
