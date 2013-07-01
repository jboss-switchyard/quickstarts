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
package org.switchyard.component.jca;

/**
 * Various constants and context variables.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public final class JCAConstants {

    /** The "jca" namespace. */
    public static final String DEFAULT_NAMESPACE = "urn:switchyard-component-jca:config:1.0";
    
    /** component name. */
    public static final String COMPONENT_NAME = "JCAComponent";
    
    /** binding. */
    public static final String BINDING = "binding";
    /** The 'JCA' implementation type. */
    public static final String JCA = "jca";

    /** outbound conection. */
    public static final String OUTBOUND_CONNECTION = "outboundConnection";
    /** inbound connection. */
    public static final String INBOUND_CONNECTION = "inboundConnection";
    /** outbound interaction. */
    public static final String OUTBOUND_INTERACTION = "outboundInteraction";
    /** inbound interaction. */
    public static final String INBOUND_INTERACTION = "inboundInteraction";
    /** wire format. */
    public static final String WIRE_FORMAT = "wireFormat";
    
    /** resource adapter. */
    public static final String RESOURCE_ADAPTER = "resourceAdapter";
    /** connection. */
    public static final String CONNECTION = "connection";
    /** activation spec. */
    public static final String ACTIVATION_SPEC = "activationSpec";
    /** connection spec. */
    public static final String CONNECTION_SPEC = "connectionSpec";
    /** interaction spec. */
    public static final String INTERACTION_SPEC = "interactionSpec";
    /** operation. */
    public static final String OPERATION = "operation";
    /** listener. */
    public static final String LISTENER = "listener";
    /** endpoint. */
    public static final String ENDPOINT = "endpoint";
    /** transacted. */
    public static final String TRANSACTED = "transacted";
    /** processor. */
    public static final String PROCESSOR = "processor";

    /** batch commit. */
    public static final String BATCH_COMMIT = "batchCommit";
    /** delay limit. */
    public static final String BATCH_TIMEOUT = "batchTimeout";
    /** batch size. */
    public static final String BATCH_SIZE = "batchSize";
    
    /** property. */
    public static final String PROPERTY = "property";
    /** name. */
    public static final String NAME = "name";
    /** value. */
    public static final String VALUE = "value";

    /** default message listener class. */
    public static final String DEFAULT_LISTENER_CLASS = "javax.resource.cci.MessageListener";

    /** CCI record name key. */
    public static final String CCI_RECORD_NAME_KEY = "recordName";
    /** CCI record short description key. */
    public static final String CCI_RECORD_SHORT_DESC_KEY = "recordShortDescription";

    /** managed. */
    public static final String MANAGED = "managed";
    /** jndi name. */
    public static final String JNDI_NAME = "jndiName";
    /** type. */
    public static final String TYPE = "type";


    private JCAConstants() {}

}
