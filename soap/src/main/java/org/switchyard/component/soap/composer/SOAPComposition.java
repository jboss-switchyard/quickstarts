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
package org.switchyard.component.soap.composer;

import javax.xml.soap.SOAPMessage;

import org.switchyard.component.common.composer.Composition;
import org.switchyard.component.common.composer.ContextMapper;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.config.model.SOAPContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;

/**
 * Utility class for SOAP-specific Composition.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class SOAPComposition {

    /** The "soap_message_header" context property label. */
    public static final String SOAP_MESSAGE_HEADER = "soap_message_header";

    /** The "soap_message_mime_header" context property label. */
    public static final String SOAP_MESSAGE_MIME_HEADER = "soap_message_mime_header";

    /**
     * Uses the {@link Composition} class to create a SOAP-specific MessageComposer.
     * @return the MessageComposer
     */
    public static MessageComposer<SOAPMessage> getMessageComposer() {
        return Composition.getMessageComposer(SOAPMessage.class);
    }

    /**
     * Uses the {@link Composition} class to create a SOAP-specific MessageComposer.
     * @param sbm a SOAPBindingModel to get configuration details from
     * @return the MessageComposer
     */
    public static MessageComposer<SOAPMessage> getMessageComposer(SOAPBindingModel sbm) {
        SOAPContextMapperModel scmm = sbm != null ? sbm.getSOAPContextMapper() : null;
        MessageComposerModel mcm = sbm != null ? sbm.getMessageComposer() : null;
        MessageComposer<SOAPMessage> mc = Composition.getMessageComposer(SOAPMessage.class, scmm, mcm);
        ContextMapper<SOAPMessage> cm = mc.getContextMapper();
        if (cm instanceof SOAPContextMapper && scmm != null) {
            ((SOAPContextMapper)cm).setSOAPHeadersType(scmm.getSOAPHeadersType());
        }
        return mc;
    }

    private SOAPComposition() {}

}
