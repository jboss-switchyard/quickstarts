/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
