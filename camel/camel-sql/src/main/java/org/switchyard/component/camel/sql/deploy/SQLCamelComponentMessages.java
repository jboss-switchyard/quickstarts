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
package org.switchyard.component.camel.sql.deploy;

import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.switchyard.SwitchYardException;

/**
 * <p/>
 * This file is using the subset 34000-34100 for logger messages.
 * <p/>
 *
 */
@MessageBundle(projectCode = "SWITCHYARD")
public interface SQLCamelComponentMessages {
    /**
     * The default messages.
     */
    SQLCamelComponentMessages MESSAGES = Messages.getBundle(SQLCamelComponentMessages.class);
    
    /**
     * periodAttributeMandatory method definition.
     * @return SwitchYardException
     */
    @Message(id = 34000, value = "Period attribute is mandatory for SQL service bindings")
    SwitchYardException periodAttributeMandatory();

    
}
