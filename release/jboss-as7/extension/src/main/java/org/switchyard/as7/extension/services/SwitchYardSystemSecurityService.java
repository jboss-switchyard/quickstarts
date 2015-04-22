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

package org.switchyard.as7.extension.services;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.switchyard.security.system.DefaultSystemSecurity;
import org.switchyard.security.system.SystemSecurity;

/**
 * SystemSecurity Service for AS7 deployments.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class SwitchYardSystemSecurityService implements Service<SystemSecurity> {

    /**
     * The name used to resolve the SystemSecurity.
     */
    public final static ServiceName SERVICE_NAME = ServiceName.of(SwitchYardSystemSecurityService.class.getSimpleName());

    private SystemSecurity _systemSecurity = null;

    @Override
    public void start(StartContext startContext) throws StartException {
        _systemSecurity = new DefaultSystemSecurity();
    }

    @Override
    public void stop(StopContext stopContext) {
        _systemSecurity = null;
    }

    @Override
    public SystemSecurity getValue() throws IllegalStateException, IllegalArgumentException {
        return _systemSecurity;
    }

}
