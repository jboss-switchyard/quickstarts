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


import org.jboss.logging.Logger;
import org.jboss.logging.annotations.MessageLogger;


/**
 * <p/>
 * This file is using the subset 33400-33799 for logger messages.
 * <p/>
 *
 */
@MessageLogger(projectCode = "SWITCHYARD")
public interface CamelComponentLogger {
    /**
     * A root logger with the category of the package name.
     */
    CamelComponentLogger ROOT_LOGGER = Logger.getMessageLogger(CamelComponentLogger.class, CamelComponentLogger.class.getPackage().getName());

}
