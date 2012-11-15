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
package org.switchyard.security.callback;

import java.security.cert.Certificate;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.security.auth.callback.Callback;

/**
 * CertificateCallback.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class CertificateCallback implements Callback {

    private Set<Certificate> _certificates = new LinkedHashSet<Certificate>();

    /**
     * Constructs a new CertificateCallback.
     */
    public CertificateCallback() {}

    /**
     * Gets the Certificates.
     * @return the Certificates
     */
    public Set<Certificate> getCertificates() {
        Set<Certificate> copy = new LinkedHashSet<Certificate>();
        copy.addAll(_certificates);
        return copy;
    }

    /**
     * Adds a Certificate.
     * @param certificate the Certificate to add
     */
    public void addCertificate(Certificate certificate) {
        if (certificate != null) {
            _certificates.add(certificate);
        }
    }

}
