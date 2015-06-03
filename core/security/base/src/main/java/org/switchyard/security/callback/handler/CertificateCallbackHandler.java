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
package org.switchyard.security.callback.handler;

import java.io.IOException;
import java.util.Set;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.switchyard.security.BaseSecurityMessages;
import org.switchyard.security.callback.CertificateCallback;
import org.switchyard.security.credential.CertificateCredential;
import org.switchyard.security.credential.Credential;

/**
 * CertificateCallbackHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class CertificateCallbackHandler extends SwitchYardCallbackHandler {

    /**
     * Constructs a new CertificateCallbackHandler.
     */
    public CertificateCallbackHandler() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        String alias = getProperty("alias", true);
        String keyPassword = getProperty("keyPassword", false);
        Set<Credential> credentials = getCredentials();
        if (credentials == null) {
            throw BaseSecurityMessages.MESSAGES.credentialsNotSet();
        }
        for (Callback cb : callbacks) {
            if (cb instanceof NameCallback) {
                ((NameCallback)cb).setName(alias);
            } else if (cb instanceof PasswordCallback && keyPassword != null) {
                ((PasswordCallback)cb).setPassword(keyPassword.toCharArray());
            } else if (cb instanceof CertificateCallback) {
                CertificateCallback cert_cb = (CertificateCallback)cb;
                for (Credential cred : credentials) {
                    if (cred instanceof CertificateCredential) {
                        cert_cb.addCertificate(((CertificateCredential)cred).getCertificate());
                    }
                }
            }
        }
    }

}
