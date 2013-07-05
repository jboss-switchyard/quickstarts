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
package org.switchyard.security.credential;

import java.security.cert.Certificate;

/**
 * CertificateCredential.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class CertificateCredential implements Credential {

    private final Certificate _certificate;

    /**
     * Constructs a CertificateCredential with the specified Certificate.
     * @param certificate the specified Certificate
     */
    public CertificateCredential(Certificate certificate) {
        _certificate = certificate;
    }

    /**
     * Gets the Certificate.
     * @return the certificate
     */
    public Certificate getCertificate() {
        return _certificate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "CertificateCredential [certificate=" + _certificate + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_certificate == null) ? 0 : _certificate.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CertificateCredential other = (CertificateCredential)obj;
        if (_certificate == null) {
            if (other._certificate != null) {
                return false;
            }
        } else if (!_certificate.equals(other._certificate)) {
            return false;
        }
        return true;
    }

}
