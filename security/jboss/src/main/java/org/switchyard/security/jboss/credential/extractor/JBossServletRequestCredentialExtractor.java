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
package org.switchyard.security.jboss.credential.extractor;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.ServletRequest;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.jboss.as.web.security.JBossGenericPrincipal;
import org.switchyard.common.type.reflect.Access;
import org.switchyard.common.type.reflect.FieldAccess;
import org.switchyard.security.credential.Credential;
import org.switchyard.security.credential.SubjectCredential;
import org.switchyard.security.credential.extractor.DefaultServletRequestCredentialExtractor;

/**
 * JBossServletRequestCredentialExtractor.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class JBossServletRequestCredentialExtractor extends DefaultServletRequestCredentialExtractor {

    private static final Access<Request> REQUEST_ACCESS;
    private static final Access<Principal> PRINCIPAL_ACCESS;
    static {
        final Access<Request> requestAccess = new FieldAccess<Request>(RequestFacade.class, "request");
        REQUEST_ACCESS = requestAccess.isReadable() ? requestAccess : null;
        final Access<Principal> principalAccess = new FieldAccess<Principal>(Request.class, "userPrincipal");
        PRINCIPAL_ACCESS = principalAccess.isReadable() ? principalAccess : null;
    }

    /**
     * Constructs a new CatalinaRequestCredentialExtractor.
     */
    public JBossServletRequestCredentialExtractor() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Credential> extract(ServletRequest source) {
        Set<Credential> credentials = new HashSet<Credential>();
        if (source != null) {
            credentials.addAll(super.extract(source));
            Request request = null;
            if (source instanceof Request) {
                request = (Request)source;
            } else if (source instanceof RequestFacade && REQUEST_ACCESS != null) {
                request = REQUEST_ACCESS.read((RequestFacade)source);
            }
            if (request != null && PRINCIPAL_ACCESS != null) {
                Principal principal = PRINCIPAL_ACCESS.read(request);
                if (principal instanceof JBossGenericPrincipal) {
                    Subject subject = ((JBossGenericPrincipal)principal).getSubject();
                    if (subject != null) {
                        credentials.add(new SubjectCredential(subject));
                    }
                }
            }
        }
        return credentials;
    }

}
