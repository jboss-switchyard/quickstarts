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

import org.switchyard.security.credential.Credential;
import org.switchyard.security.credential.NameCredential;
import org.switchyard.security.credential.PasswordCredential;

/**
 * NamePasswordCallbackHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class NamePasswordCallbackHandler extends SwitchYardCallbackHandler {

    /**
     * Constructs a new NamePasswordCallbackHandler.
     */
    public NamePasswordCallbackHandler() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        Set<Credential> credentials = getCredentials();
        if (credentials == null) {
            throw new IllegalStateException("Credentials not set");
        }
        for (Callback cb : callbacks) {
            if (cb instanceof NameCallback) {
                for (Credential cred : credentials) {
                    if (cred instanceof NameCredential) {
                        ((NameCallback)cb).setName(((NameCredential)cred).getName());
                    }
                }
            } else if (cb instanceof PasswordCallback) {
                for (Credential cred : credentials) {
                    if (cred instanceof PasswordCredential) {
                        ((PasswordCallback)cb).setPassword(((PasswordCredential)cred).getPassword());
                    }
                }
            }
        }
    }

}
