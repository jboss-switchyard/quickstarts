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
package org.switchyard.component.camel.common;

/**
 * Constants used by Camel component.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public interface CamelConstants {

    /**
     * SwitchYard component scheme.
     */
    String SWITCHYARD_COMPONENT_NAME = "switchyard";

    /**
     * Name of message header where operation selector is stored.
     */
    String OPERATION_SELECTOR_HEADER = "org.switchyard.operationSelector";

    /**
     * Name of message header where message composer is stored.
     */
    String MESSAGE_COMPOSER_HEADER = "org.switchyard.messageComposer";
    
    /**
     * Namespace of the application.
     */
    String APPLICATION_NAMESPACE = "org.switchyard.component.camel.namespace";

    /**
     * Name of Spring SPI transaction policy bean.
     */
    String TRANSACTED_REF = "transactionPolicy";

}
