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
