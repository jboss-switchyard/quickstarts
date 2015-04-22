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

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.switchyard.security.callback.handler.NamePasswordCallbackHandler;
import org.switchyard.security.callback.handler.SwitchYardCallbackHandler;

/**
 * STSIssueCallbackHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class STSIssueCallbackHandler extends SwitchYardCallbackHandler {

    /**
     * Constructs a new STSIssueCallbackHandler.
     */
    public STSIssueCallbackHandler() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        handle(callbacks, new NamePasswordCallbackHandler());
        handle(callbacks, new STSTokenCallbackHandler());
    }

    private void handle(Callback[] callbacks, SwitchYardCallbackHandler handler) throws IOException, UnsupportedCallbackException {
        handler.setProperties(getProperties());
        handler.setCredentials(getCredentials());
        handler.handle(callbacks);
    }

}
