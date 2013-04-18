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
           WSPasswordCallback pc = (WSPasswordCallback)arg0[i];
           String password = _passwords.get(pc.getIdentifier());
           if (password != null) {
              pc.setPassword(password);
              return;
           }
        }
    }

}
