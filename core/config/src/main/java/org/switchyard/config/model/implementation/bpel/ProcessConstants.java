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
package org.switchyard.config.model.implementation.bpel;

import javax.xml.namespace.QName;

/**
 * Various constants and context variables.
 *
 */
public final class ProcessConstants {

    private ProcessConstants() {
    }
    
    /**
     * The default process namespace.
     */
    public static final String PROCESS_NAMESPACE = "http://docs.oasis-open.org/ns/opencsa/sca/200912";

    /** processDescriptor . */
    public static final String PROCESS = "process";
    
    /** {http://docs.oasis-open.org/ns/opencsa/sca/200912}process . */
    public static final String PROCESS_VAR = new QName(PROCESS_NAMESPACE, PROCESS).toString();

}
