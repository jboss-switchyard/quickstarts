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
package org.switchyard;

import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.ServiceOperation;

/**
 * An Exchange represents an instance of a service invocation with a specific
 * message exchange pattern (e.g. InOnly, InOut).  Exchange provides a conduit
 * for the messages that flow into and out of a service  as part of a service
 * invocation.  Unlike messages, an exchange cannot be copied and reused across
 * service invocations.  State associated with  an invocation (i.e. context) is
 * maintained at the exchange level.
 */
public interface Exchange {

    /**
     * Context property name used for Message ID.
     */
    String MESSAGE_ID = "org.switchyard.messageId";

    /**
     * Context property name used for Relates To.
     */
    String RELATES_TO = "org.switchyard.relatesTo";

    /**
     * Context property name used for Message Content Type.
     */
    String CONTENT_TYPE = "org.switchyard.contentType";

    /**
     * Context property name used for Operation Name.
     */
    String OPERATION_NAME = "org.switchyard.operationName";

    /**
     * Context property name used for Fault Type.
     */
    String FAULT_TYPE = "org.switchyard.faultType";

    /**
     * Context property name used for Service Name.
     */
    String SERVICE_NAME = "org.switchyard.serviceName";

    /** 
     * Context property name used for A boolean flag which indicates if the transaction should
     *  be rolled back or not on fault.
     *  */
    String ROLLBACK_ON_FAULT = "org.switchyard.rollbackOnFault";

    /**
     * Retrieves the exchange context.
     * @return the exchange context
     */
    Context getContext();

    /**
     * Retrieves context available for passed message. If message is same as
     * message available on this exchange it will return same context as {@link #getContext()}
     * method, otherwise another instance of Context will be returned.
     * 
     * @param message Message instance.
     * @return Context for message.
     */
    Context getContext(Message message);

    /**
     * Get the service reference representing the consumer.
     * @return The consumer service reference instance.
     */
    ServiceReference getConsumer();
    
    /**
     * Get the service representing the provider.  This method will return
     * null before an exchange is sent for the first time.
     * @return the provider's service
     */
    Service getProvider();
    
    /**
     * The contract between consumer and provider for this exchange.
     * @return exchange contract
     */
    ExchangeContract getContract();
    
    /**
     * Set service consumer info for this exchange.
     * @param consumer the service reference used by the exchange
     * @param operation the operation being invoked
     * @return this Exchange instance
     */
    Exchange consumer(ServiceReference consumer, ServiceOperation operation);
    
    /**
     * Set service provider info for this exchange.
     * @param provider the service provider used by the exchange
     * @param operation the operation being invoked
     * @return this Exchange instance
     */
    Exchange provider(Service provider, ServiceOperation operation);

    /**
     * Returns the current message for the exchange.  On new exchanges, this
     * method will always return null.  The current message reference is updated
     * with each call to send().
     * @return the current message for the exchange
     */
    Message getMessage();
    
    /**
     * Create a message to be used with this exchange.
     * @return new message
     */
    Message createMessage();

    /**
     * Sends the specified message as part of this message exchange.
     * <p/>
     * Implementations must throw an {@link IllegalStateException} if this method is
     * called when the Exchange {@link #getState() state} is in {@link ExchangeState#FAULT}.
     * @param message message to send
     */
    void send(Message message);

    /**
     * Sends the specified message as part of this message exchange.
     * <p/>
     * Also sets the Exchange {@link #getState() state} to {@link ExchangeState#FAULT}.
     * @param message message to send
     */
    void sendFault(Message message);

    /**
     * Get the exchange state.
     * @return The exchange State.
     */
    ExchangeState getState();

    /**
     * Get the exchange phase.
     * @return the exchange phase
     */
    ExchangePhase getPhase();

    /**
     * Get the reply handler for this exchange.
     * @return reply handler
     */
    ExchangeHandler getReplyHandler();

    /**
     * Provides the consumer's view of the message exchange pattern.  This 
     * method is equivalent to getContract().getConsumerOperation().getExchangePattern().
     * Note that if consumer information has not been set, this method will return null!
     * @return the consumer's exchange pattern or null if no consumer info is set on the exchange
     */
    ExchangePattern getPattern();
}
