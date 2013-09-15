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
package org.switchyard.component.camel;

import static org.jboss.logging.Logger.Level.ERROR;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 * <p/>
 * This file is using the subset 33800-33899 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface SwitchYardCamelComponentLogger {
    /**
     * A root logger with the category of the package name.
     */
    SwitchYardCamelComponentLogger ROOT_LOGGER = Logger.getMessageLogger(SwitchYardCamelComponentLogger.class, SwitchYardCamelComponentLogger.class.getPackage().getName());

    /**
     * cannotLookupOperation method definition.
     * @param e e
     */
    @LogMessage(level = ERROR)
    @Message(id = 33400, value = "Cannot lookup operation using custom operation selector. Returning empty name")
    void cannotLookupOperation(@Cause Exception e);
}
