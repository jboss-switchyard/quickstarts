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
