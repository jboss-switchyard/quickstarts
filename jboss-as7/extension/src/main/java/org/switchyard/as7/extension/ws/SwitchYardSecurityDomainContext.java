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
 
package org.switchyard.as7.extension.ws;

import java.security.Principal;
import java.util.Set;

import javax.security.auth.Subject;


import org.jboss.as.webservices.security.SecurityDomainContextAdaptor;
import org.jboss.wsf.spi.security.SecurityDomainContext;

public class SwitchYardSecurityDomainContext implements SecurityDomainContext {

    private SecurityDomainContext _context;
    private String _domain;

    public SwitchYardSecurityDomainContext(String domain, SecurityDomainContext context) {
        _domain = domain;
        _context = context;
    }

    @Override
    public boolean isValid(Principal principal, Object credential, Subject activeSubject) {
        return _context.isValid(principal, credential, activeSubject);
    }

    @Override
    public boolean doesUserHaveRole(Principal principal, Set<Principal> roles) {
        return _context.doesUserHaveRole(principal, roles);
    }

    @Override
    public String getSecurityDomain() {
        if (_domain != null) {
            return _domain;
        } else {
            return _context.getSecurityDomain();
        }
    }

    @Override
    public Set<Principal> getUserRoles(Principal principal) {
        return _context.getUserRoles(principal);
    }

    @Override
    public void pushSubjectContext(final Subject subject, final Principal principal, final Object credential) {
        _context.pushSubjectContext(subject, principal, credential);
    }
}
