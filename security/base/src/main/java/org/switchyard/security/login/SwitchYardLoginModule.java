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
package org.switchyard.security.login;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.switchyard.common.lang.Strings;

/**
 * SwitchYardLoginModule.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class SwitchYardLoginModule implements LoginModule {

    private Subject _subject;
    private CallbackHandler _callbackHandler;
    private Map<String, ?> _sharedState;
    private Map<String, ?> _options;

    /**
     * Gets the initialized Subject.
     * @return the Subject
     */
    public Subject getSubject() {
        return _subject;
    }

    /**
     * Gets the initialized CallbackHandler.
     * @return the CallbackHandler
     */
    public CallbackHandler getCallbackHandler() {
        return _callbackHandler;
    }

    /**
     * Gets the initialized shared state.
     * @return the shared state
     */
    public Map<String, ?> getSharedState() {
        return _sharedState;
    }

    /**
     * Gets the initialized options.
     * @return the options
     */
    public Map<String, ?> getOptions() {
        return _options;
    }

    /**
     * Gets an option value with the specified name, as a String.
     * @param name the specified name
     * @return the option value
     */
    public String getOption(String name) {
        return getOption(name, false);
    }

    /**
     * Gets an option value with the specified name, as a String.
     * @param name the specified name
     * @param required if the option is required to have been set
     * @return the option value
     */
    public String getOption(String name, boolean required) {
        Map<String, ?> options = getOptions();
        if (options == null) {
            if (required) {
                throw new IllegalStateException("options not set");
            }
        } else {
            Object value = options.get(name);
            if (value != null) {
                return Strings.replaceSystemProperties(String.valueOf(value));
            } else if (required) {
                throw new IllegalStateException("option [" + name + "] not set");
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        _subject = subject;
        _callbackHandler = callbackHandler;
        _sharedState = sharedState;
        _options = options;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean login() throws LoginException {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean commit() throws LoginException {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean abort() throws LoginException {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean logout() throws LoginException {
        return true;
    }

}
