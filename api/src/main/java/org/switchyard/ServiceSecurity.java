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
package org.switchyard;

import java.util.Map;
import java.util.Set;

/**
 * ServiceSecurity.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public interface ServiceSecurity {

    /**
     * The default name ("default").
     */
    public static final String DEFAULT_NAME = "default";

    /**
     * The default security domain ("other").
     */
    public static final String DEFAULT_SECURITY_DOMAIN = "other";

    /**
     * Gets the name.
     * @return the name
     */
    public String getName();

    /**
     * Gets the CallbackHandler class.
     * @return the CallbackHandler class
     */
    public Class<?> getCallbackHandler();

    /**
     * Gets the properties.
     * @return the properties
     */
    public Map<String,String> getProperties();

    /**
     * Gets the roles allowed.
     * @return the roles allowed
     */
    public Set<String> getRolesAllowed();

    /**
     * Gets the run as.
     * @return the run as
     */
    public String getRunAs();

    /**
     * Gets the security domain.
     * @return the security domain
     */
    public String getSecurityDomain();

}
