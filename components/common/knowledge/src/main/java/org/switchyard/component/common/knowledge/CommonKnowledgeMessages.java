/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.jboss.logging.Cause;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.HandlerException;
import org.switchyard.SwitchYardException;

/**
 * <p/>
 * This file is using the subset 34700-34799 for logger messages.
 * <p/>
 *
 */
/**
 * @author tcunning
 *
 */
@SuppressWarnings("deprecation")
@MessageBundle(projectCode = "SWITCHYARD")
public interface CommonKnowledgeMessages {
    /**
     * The default messages.
     */
    CommonKnowledgeMessages MESSAGES = Messages.getBundle(CommonKnowledgeMessages.class);

    /**
     * unknownExpressionType method definition.
     * @param expressionType the expressionType
     * @return IllegalArgumentException
     */
    @Message(id = 34700, value = "Unknown expression type: %s")
    IllegalArgumentException unknownExpressionType(String expressionType);

    /**
     * serviceNameNull method definition.
     * @return SwitchYardException
     */
    @Message(id = 34701, value = "ServiceName == null")
    SwitchYardException serviceNameNull();

    /**
     * serviceDomainNull method definition.
     * @return SwitchYardException
     */
    @Message(id = 34702, value = "ServiceDomain == null")
    SwitchYardException serviceDomainNull();

    /**
     * serviceReferenceNull method definition.
     * @param serviceName the serviceName
     * @return SwitchYardException
     */
    @Message(id = 34703, value = "ServiceReference [%s] == null")
    SwitchYardException serviceReferenceNull(String serviceName);

    /**
     * manifestContainerRequiredInConfigurationForPersistentSessions method definition.
     * @return SwitchYardException
     */
    @Message(id = 34706, value = "manifest container required in configuration for persistent sessions")
    SwitchYardException manifestContainerRequiredInConfigurationForPersistentSessions();

    /**
     * containerScanIntervalMustBePositive method definition.
     * @return IllegalArgumentException
     */
    @Message(id = 34707, value = "container scanInterval must be positive")
    IllegalArgumentException containerScanIntervalMustBePositive();

    /**
     * couldNotUseNullNameToRegisterChannel method definition.
     * @param channelClassName channelClassName
     * @return SwitchYardException
     */
    @Message(id = 34708, value = "Could not use null name to register channel: %s")
    SwitchYardException couldNotUseNullNameToRegisterChannel(String channelClassName);

    /**
     * couldNotLoadListenerClass method definition.
     * @param listenerModelClass listenerModelClass
     * @return SwitchYardException
     */
    @Message(id = 34709, value = "Could not load listener class: %s")
    SwitchYardException couldNotLoadListenerClass(String listenerModelClass);

    /**
     * couldNotInstantiateListenerClass method definition.
     * @param listenerClassName listenerClassName
     * @return SwitchYardException
     */
    @Message(id = 34710, value = "Could not instantiate listener class: %s")
    SwitchYardException couldNotInstantiateListenerClass(String listenerClassName);

    /**
     * cannotRegisterOperation method definition.
     * @param type type
     * @param name name
     * @return SwitchYardException
     */
    @Message(id = 34711, value = "cannot register %s operation due to duplicate name: %s")
    SwitchYardException cannotRegisterOperation(String type, String name);
    
    /**
     * problemBuildingKnowledge method definition.
     * @return String
     */
    @Message(id = 34712, value = "Problem building knowledge")
    String problemBuildingKnowledge();

    /**
     * faultEncountered method definition.
     * @return String
     */
    @Message(id = 34713, value = "Fault encountered")
    String faultEncountered();

    /**
     * userTransactionBeginFailed method definition.
     * @param se the se
     * @return HandlerException
     */
    @Message(id = 34714, value = "UserTransaction begin failed")
    HandlerException userTransactionBeginFailedSystem(@Cause SystemException se);

    /**
     * userTransactionBeginFailed method definition.
     * @param nse the nse
     * @return HandlerException
     */
    @Message(id = 34715, value = "UserTransaction begin failed")
    HandlerException userTransactionBeginFailedNSE(@Cause NotSupportedException nse);

    /**
     * userTransactionCommitFailed method definition.
     * @param se the se
     * @return HandlerException
     */
    @Message(id = 34716, value = "UserTransaction commit failed")
    HandlerException userTransactionCommitFailedSystem(@Cause SystemException se);

    /**
     * userTransactionCommitFailed method definition.
     * @param hre the hre
     * @return HandlerException
     */
    @Message(id = 34717, value = "UserTransaction commit failed")
    HandlerException userTransactionCommitFailedRollback(@Cause HeuristicRollbackException hre);

    /**
     * userTransactionCommitFailed method definition.
     * @param hme the hme
     * @return HandlerException
     */
    @Message(id = 34718, value = "UserTransaction commit failed")
    HandlerException userTransactionCommitFailedMixed(@Cause HeuristicMixedException hme);

    /**
     * userTransactionCommitFailed method definition.
     * @param re the re
     * @return HandlerException
     */
    @Message(id = 34719, value = "UserTransaction commit failed")
    HandlerException userTransactionCommitFailed(@Cause RollbackException re);

    /**
     * userTransactionRollbackFailed method definition.
     * @param se the se
     * @return HandlerException
     */
    @Message(id = 34720, value = "UserTransaction rollback failed")
    HandlerException userTransactionRollbackFailed(@Cause SystemException se);

    /**
     * userTransactionSetRollbackOnlyFailed method definition.
     * @param se the se
     * @return HandlerException
     */
    @Message(id = 34721, value = "UserTransaction setRollbackOnly failed")
    HandlerException userTransactionSetRollbackOnlyFailed(@Cause SystemException se);

    /**
     * couldNotInstantiateUserGroupCallbackClass method definition.
     * @param callbackClassName callbackClassName
     * @return SwitchYardException
     */
    @Message(id = 34722, value = "Could not instantiate userGroupCallback class: %s")
    SwitchYardException couldNotInstantiateUserGroupCallbackClass(String callbackClassName);

    /**
     * couldNotLoadWorkItemHandlerClass method definition.
     * @param workItemHandlerModelClass workItemHandlerModelClass
     * @return SwitchYardException
     */
    @Message(id = 34723, value = "Could not load workItemHandler class: %s")
    SwitchYardException couldNotLoadWorkItemHandlerClass(String workItemHandlerModelClass);

    /**
     * couldNotUseNullNameToRegisterWorkItemHandler method definition.
     * @param workItemHandlerClassName workItemHandlerClassName
     * @return SwitchYardException
     */
    @Message(id = 34724, value = "Could not use null name to register workItemHandler: %s")
    SwitchYardException couldNotUseNullNameToRegisterWorkItemHandler(String workItemHandlerClassName);

    /**
     * couldNotInstantiateWorkItemHandlerClass method definition.
     * @param workItemHandlerClassName workItemHandlerClassName
     * @return SwitchYardException
     */
    @Message(id = 34725, value = "Could not instantiate workItemHandler class: %s")
    SwitchYardException couldNotInstantiateWorkItemHandlerClass(String workItemHandlerClassName);
}

