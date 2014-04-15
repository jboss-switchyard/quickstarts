/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.security;

import org.switchyard.common.util.ProviderRegistry;
import org.switchyard.security.credential.extractor.DefaultServletRequestCredentialExtractor;
import org.switchyard.security.credential.extractor.ServletRequestCredentialExtractor;
import org.switchyard.security.provider.JaasSecurityProvider;
import org.switchyard.security.provider.SecurityProvider;

/**
 * SecurityServices.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public final class SecurityServices {

    private SecurityServices() {}

    /**
     * Gets the SecurityProvider.
     * @return the SecurityProvider
     */
    public static final SecurityProvider getSecurityProvider() {
        SecurityProvider sp = getProvider(SecurityProvider.class);
        return sp != null ? sp : new JaasSecurityProvider();
    }

    /**
     * Gets the ServletRequestCredentialExtractor.
     * @return the ServletRequestCredentialExtractor
     */
    public static final ServletRequestCredentialExtractor getServletRequestCredentialExtractor() {
        ServletRequestCredentialExtractor srce = getProvider(ServletRequestCredentialExtractor.class);
        return srce != null ? srce : new DefaultServletRequestCredentialExtractor();
    }

    private static final <T> T getProvider(Class<T> clazz) {
        return ProviderRegistry.getProvider(clazz, clazz.getClassLoader());
    }

}
