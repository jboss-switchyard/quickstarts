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
package org.switchyard.security.karaf.provider;

import org.apache.karaf.jaas.boot.ProxyLoginModule;
import org.switchyard.ServiceSecurity;
import org.switchyard.security.context.SecurityContext;
import org.switchyard.security.provider.DefaultSecurityProvider;

/**
 * KarafSecurityProvider.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class KarafSecurityProvider extends DefaultSecurityProvider {

    static {
        // Here to trigger fallback usage of a different SecurityProvider (or DefaultSecurityProvider),
        // if the org.apache.karaf.jaas:org.apache.karaf.jaas.modules dependency is not available.
        new ProxyLoginModule();
    }

    /**
     * Constructs a new KarafSecurityProvider.
     */
    public KarafSecurityProvider() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(ServiceSecurity serviceSecurity, SecurityContext securityContext) {
        super.populate(serviceSecurity, securityContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear(ServiceSecurity serviceSecurity, SecurityContext securityContext) {
        super.clear(serviceSecurity, securityContext);
    }

}
