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
package org.switchyard.security.login;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.switchyard.common.lang.Strings;
import org.switchyard.security.BaseSecurityMessages;

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
                throw BaseSecurityMessages.MESSAGES.optionsNotSet();
            }
        } else {
            Object value = options.get(name);
            if (value != null) {
                return Strings.replaceSystemAndTestProperties(String.valueOf(value));
            } else if (required) {
                throw BaseSecurityMessages.MESSAGES.optionNotSet(name);
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
