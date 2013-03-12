/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.security.credential.extractor;

import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

import org.switchyard.security.credential.CertificateCredential;
import org.switchyard.security.credential.ConfidentialityCredential;
import org.switchyard.security.credential.Credential;
import org.switchyard.security.credential.PrincipalCredential;

/**
 * CredentialExtractor which extracts {@link Credential}s from a given {@link SSLSession}.
 */
public class SSLSessionCredentialExtractor implements CredentialExtractor<SSLSession> {

    /**
     * Constructs a new SSLSessionCredentialExtractor.
     */
    public SSLSessionCredentialExtractor() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Credential> extract(SSLSession source) {
        Set<Credential> credentials = new HashSet<Credential>();
        try {
            credentials.add(new ConfidentialityCredential(source.isValid()));
            credentials.add(new PrincipalCredential(source.getPeerPrincipal()));
            credentials.add(new CertificateCredential(source.getPeerCertificates()[0]));
        } catch (SSLPeerUnverifiedException e) {
            throw new RuntimeException("Unable to extract Credentials from SSLSession: " + e.getMessage(), e);
        }
        return credentials;
    }

}
