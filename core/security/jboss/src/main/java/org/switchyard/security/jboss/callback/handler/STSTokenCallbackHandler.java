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
package org.switchyard.security.jboss.callback.handler;

import java.io.IOException;
import java.util.Set;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.picketlink.identity.federation.core.wstrust.auth.TokenCallback;
import org.switchyard.security.callback.handler.SwitchYardCallbackHandler;
import org.switchyard.security.credential.AssertionCredential;
import org.switchyard.security.credential.Credential;
import org.switchyard.security.jboss.JBossSecurityMessages;

/**
 * STSTokenCallbackHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class STSTokenCallbackHandler extends SwitchYardCallbackHandler {

    /**
     * Constructs a new STSTokenCallbackHandler.
     */
    public STSTokenCallbackHandler() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        Set<Credential> credentials = getCredentials();
        if (credentials == null) {
            throw JBossSecurityMessages.MESSAGES.credentialsNotSet();
        }
        for (Callback cb : callbacks) {
            if (cb instanceof TokenCallback) {
                for (Credential cred : credentials) {
                    if (cred instanceof AssertionCredential) {
                        ((TokenCallback)cb).setToken(((AssertionCredential)cred).getAssertion());
                    }
                }
            }
        }
    }

}
