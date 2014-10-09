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
package org.switchyard.component.rules;

import javax.xml.namespace.QName;

/**
 * Rules constants.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class RulesConstants {

    /** urn:switchyard-component-rules:rules:1.0 . */
    public static final String RULES_NAMESPACE = "urn:switchyard-component-rules:rules:1.0";

    /**
     * sessionId .
     * @deprecated
     */
    @Deprecated
    public static final String SESSION_ID = "sessionId";
    /**
     * {urn:switchyard-component-rules:rules:1.0}sessionId .
     * @deprecated
     */
    @Deprecated
    public static final String SESSION_ID_PROPERTY = new QName(RULES_NAMESPACE, SESSION_ID).toString();

    /** continue . */
    public static final String CONTINUE = "continue";
    /** {urn:switchyard-component-rules:rules:1.0}continue . */
    public static final String CONTINUE_PROPERTY = new QName(RULES_NAMESPACE, CONTINUE).toString();

    /** dispose . */
    public static final String DISPOSE = "dispose";
    /** {urn:switchyard-component-rules:rules:1.0}dispose . */
    public static final String DISPOSE_PROPERTY = new QName(RULES_NAMESPACE, DISPOSE).toString();

    private RulesConstants() {}

}
