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
 
package org.switchyard.component.http.composer;

import org.switchyard.component.common.composer.Composition;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.http.config.model.HttpBindingModel;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;

/**
 * Utility class for HTTP-specific Composition.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public final class HttpComposition {

    /** The http_request_info property name. */
    public static final String HTTP_REQUEST_INFO = "http_request_info";

    /**
     * Uses the {@link Composition} class to create a HTTP-specific MessageComposer.
     * @return the MessageComposer
     */
    public static MessageComposer<HttpBindingData> getMessageComposer() {
        return Composition.getMessageComposer(HttpBindingData.class);
    }

    /**
     * Uses the {@link Composition} class to create a HTTP-specific MessageComposer.
     * @param hbm a HttpBindingModel to get configuration details from
     * @return the MessageComposer
     */
    public static MessageComposer<HttpBindingData> getMessageComposer(HttpBindingModel hbm) {
        ContextMapperModel cmm = hbm != null ? hbm.getContextMapper() : null;
        MessageComposerModel mcm = hbm != null ? hbm.getMessageComposer() : null;
        MessageComposer<HttpBindingData> mc = Composition.getMessageComposer(HttpBindingData.class, cmm, mcm);
        if (mc instanceof HttpMessageComposer && mcm != null) {
            HttpMessageComposer smc = (HttpMessageComposer)mc;
            smc.setComposerConfig(mcm);
        }
        return mc;
    }

    private HttpComposition() {}

}
