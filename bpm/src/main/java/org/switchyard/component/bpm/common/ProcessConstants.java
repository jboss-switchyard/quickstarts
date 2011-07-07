/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.switchyard.component.bpm.common;

import javax.xml.namespace.QName;

/**
 * Various constants and context variables.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class ProcessConstants {

    /**
     * The default process namespace.
     */
    public static final String PROCESS_NAMESPACE = "urn:switchyard-component-bpm:process:1.0";

    /** processActionType . */
    public static final String PROCESS_ACTION_TYPE = "processActionType";
    /** {urn:switchyard-component-bpm:process:1.0}processActionType . */
    public static final String PROCESS_ACTION_TYPE_VAR = new QName(PROCESS_NAMESPACE, PROCESS_ACTION_TYPE).toString();

    /** processDefinition . */
    public static final String PROCESS_DEFINITION = "processDefinition";
    /** {urn:switchyard-component-bpm:process:1.0}processDefinition . */
    public static final String PROCESS_DEFINITION_VAR = new QName(PROCESS_NAMESPACE, PROCESS_DEFINITION).toString();

    /** processDefinitionType . */
    public static final String PROCESS_DEFINITION_TYPE = "processDefinitionType";
    /** {urn:switchyard-component-bpm:process:1.0}processDefinitionType . */
    public static final String PROCESS_DEFINITION_TYPE_VAR = new QName(PROCESS_NAMESPACE, PROCESS_DEFINITION_TYPE).toString();

    /** processEvent . */
    public static final String PROCESS_EVENT = "processEvent";
    /** {urn:switchyard-component-bpm:process:1.0}processEvent . */
    public static final String PROCESS_EVENT_VAR = new QName(PROCESS_NAMESPACE, PROCESS_EVENT).toString();

    /** processEventType . */
    public static final String PROCESS_EVENT_TYPE = "processEventType";
    /** {urn:switchyard-component-bpm:process:1.0}processEventType . */
    public static final String PROCESS_EVENT_TYPE_VAR = new QName(PROCESS_NAMESPACE, PROCESS_EVENT_TYPE).toString();

    /** processId . */
    public static final String PROCESS_ID = "processId";
    /** {urn:switchyard-component-bpm:process:1.0}processId . */
    public static final String PROCESS_ID_VAR = new QName(PROCESS_NAMESPACE, PROCESS_ID).toString();

    /** processInstanceId . */
    public static final String PROCESS_INSTANCE_ID = "processInstanceId";
    /** {urn:switchyard-component-bpm:process:1.0}processInstanceId . */
    public static final String PROCESS_INSTANCE_ID_VAR = new QName(PROCESS_NAMESPACE, PROCESS_INSTANCE_ID).toString();

    /** messageContent . */
    public static final String MESSAGE_CONTENT = "messageContent";
    /** {urn:switchyard-component-bpm:process:1.0}messageContent . */
    public static final String MESSAGE_CONTENT_VAR = new QName(PROCESS_NAMESPACE, MESSAGE_CONTENT).toString();

    /** messageContentName . */
    public static final String MESSAGE_CONTENT_NAME = "messageContentName";
    /** {urn:switchyard-component-bpm:process:1.0}messageContentName . */
    public static final String MESSAGE_CONTENT_NAME_VAR = new QName(PROCESS_NAMESPACE, MESSAGE_CONTENT_NAME).toString();

    /** startProcess . */
    public static final String START_PROCESS = "startProcess";
    /** {urn:switchyard-component-bpm:process:1.0}startProcess . */
    public static final String START_PROCESS_VAR = new QName(PROCESS_NAMESPACE, START_PROCESS).toString();

    /** signalEvent . */
    public static final String SIGNAL_EVENT = "signalEvent";
    /** {urn:switchyard-component-bpm:process:1.0}signalEvent . */
    public static final String SIGNAL_EVENT_VAR = new QName(PROCESS_NAMESPACE, SIGNAL_EVENT).toString();

    /** abortProcessInstance . */
    public static final String ABORT_PROCESS_INSTANCE = "abortProcessInstance";
    /** {urn:switchyard-component-bpm:process:1.0}abortProcessInstance . */
    public static final String ABORT_PROCESS_INSTANCE_VAR = new QName(PROCESS_NAMESPACE, ABORT_PROCESS_INSTANCE).toString();

    private ProcessConstants() {}

}
