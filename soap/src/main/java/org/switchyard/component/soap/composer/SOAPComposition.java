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
package org.switchyard.component.soap.composer;

import org.switchyard.component.common.composer.Composition;
import org.switchyard.component.common.composer.ContextMapper;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.config.model.SOAPContextMapperModel;
import org.switchyard.component.soap.config.model.SOAPMessageComposerModel;

/**
 * Utility class for SOAP-specific Composition.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class SOAPComposition {

    /** The soap_fault_info property name. */
    public static final String SOAP_FAULT_INFO = "soap_fault_info";

    /**
     * Uses the {@link Composition} class to create a SOAP-specific MessageComposer.
     * @return the MessageComposer
     */
    public static MessageComposer<SOAPBindingData> getMessageComposer() {
        return Composition.getMessageComposer(SOAPBindingData.class);
    }

    /**
     * Uses the {@link Composition} class to create a SOAP-specific MessageComposer.
     * @param sbm a SOAPBindingModel to get configuration details from
     * @return the MessageComposer
     */
    public static MessageComposer<SOAPBindingData> getMessageComposer(SOAPBindingModel sbm) {
        SOAPContextMapperModel scmm = sbm != null ? sbm.getSOAPContextMapper() : null;
        SOAPMessageComposerModel mcm = sbm != null ? sbm.getSOAPMessageComposer() : null;
        MessageComposer<SOAPBindingData> mc = Composition.getMessageComposer(SOAPBindingData.class, scmm, mcm);
        ContextMapper<SOAPBindingData> cm = mc.getContextMapper();
        if (cm instanceof SOAPContextMapper && scmm != null) {
            ((SOAPContextMapper)cm).setSOAPHeadersType(scmm.getSOAPHeadersType());
        }
        return mc;
    }

    private SOAPComposition() {}

}
