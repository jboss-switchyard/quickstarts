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
package org.switchyard.component.camel.common.model.consumer;

/**
 * Scheduled batch poll consumer interface. Allows to limit number of elements
 * in one poll.
 * 
 * @author Lukasz Dywicki
 */
public interface CamelScheduledBatchPollConsumer extends CamelScheduledPollConsumer {

    /**
     * An integer that defines the maximum number of messages to gather per poll.
     * 
     * @return maximum number of messages per poll.
     */
    Integer getMaxMessagesPerPoll();

    /**
     * Specify the maximum number of messages to gather per poll.
     * 
     * @param maxMessagesPerPoll the maximum number of messages to gather per poll
     * @return a reference to this binding model.
     */
    CamelScheduledBatchPollConsumer setMaxMessagesPerPoll(Integer maxMessagesPerPoll);

}
