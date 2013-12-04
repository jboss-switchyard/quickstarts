/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.demo.policy.security.wss.signencrypt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

public class WorkServiceCallbackHandler implements CallbackHandler {

    private Map<String, String> _passwords = new HashMap<String, String>();

    public WorkServiceCallbackHandler() {
        _passwords.put("alice", "password");
        _passwords.put("bob", "password");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(Callback[] arg0) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < arg0.length; i++) {
            WSPasswordCallback pc = (WSPasswordCallback) arg0[i];
            String password = _passwords.get(pc.getIdentifier());
            if (password != null) {
                pc.setPassword(password);
                return;
            }
        }
    }

}
