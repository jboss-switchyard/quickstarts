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
package org.switchyard.security.system;

import java.util.UUID;

import org.switchyard.security.crypto.PrivateCrypto;
import org.switchyard.security.crypto.PublicCrypto;

/**
 * SystemSecurity.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public interface SystemSecurity {

    /**
     * The shared/immutable/default SystemSecurity, for when one is not configured by a container.
     */
    public static final SystemSecurity DEFAULT = new DefaultSystemSecurity.ImmutableDefaultSystemSecurity();

    /**
     * Gets the universally unique identifier of this SystemSecurity.
     * @return the UUID
     */
    public UUID getUUID();

    /**
     * Gets the security context timeout in milliseconds.
     * @return the security context timeout in milliseconds
     */
    public Long getSecurityContextTimeoutMillis();

    /**
     * Gets the private crypto.
     * @return the private crypto
     */
    public PrivateCrypto getPrivateCrypto();

    /**
     * Gets the public crypto.
     * @return the public crypto
     */
    public PublicCrypto getPublicCrypto();

}
