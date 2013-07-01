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
package org.switchyard.component.bpm;

import javax.xml.namespace.QName;

/**
 * BPM constants.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class BPMConstants {

    /** urn:switchyard-component-bpm:bpm:1.0 . */
    public static final String BPM_NAMESPACE = "urn:switchyard-component-bpm:bpm:1.0";

    /** sessionId . */
    public static final String SESSION_ID = "sessionId";
    /** {urn:switchyard-component-bpm:bpm:1.0}sessionId . */
    public static final String SESSION_ID_PROPERTY = new QName(BPM_NAMESPACE, SESSION_ID).toString();

    /** processInstanceId . */
    public static final String PROCESS_INSTANCE_ID = "processInstanceId";
    /** {urn:switchyard-component-bpm:bpm:1.0}processInstanceId . */
    public static final String PROCESSS_INSTANCE_ID_PROPERTY = new QName(BPM_NAMESPACE, PROCESS_INSTANCE_ID).toString();

    /** correlationKey . */
    public static final String CORRELATION_KEY = "correlationKey";
    /** {urn:switchyard-component-bpm:bpm:1.0}correlationKey . */
    public static final String CORRELATION_KEY_PROPERTY = new QName(BPM_NAMESPACE, CORRELATION_KEY).toString();

    private BPMConstants() {}

}
