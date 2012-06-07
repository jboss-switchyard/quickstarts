/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.security.credential.extract;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.xml.ws.WebServiceContext;

import org.switchyard.security.credential.Credential;
import org.switchyard.security.credential.PrincipalCredential;

/**
 * WebServiceContextCredentialsExtractor.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class WebServiceContextCredentialsExtractor implements CredentialsExtractor<WebServiceContext> {

    /**
     * Constructs a new WebServiceContextCredentialsExtractor.
     */
    public WebServiceContextCredentialsExtractor() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Credential> extractCredentials(WebServiceContext source) {
        Set<Credential> credentials = new HashSet<Credential>();
        if (source != null) {
            Principal userPrincipal = source.getUserPrincipal();
            if (userPrincipal != null) {
                credentials.add(new PrincipalCredential(userPrincipal, true));
            }
        }
        return credentials;
    }

}
