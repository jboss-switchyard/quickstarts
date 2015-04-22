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
 
package org.switchyard.component.resteasy;

import org.switchyard.SwitchYardException;

/**
 * Wrapper for RESTEasy creation exceptions.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class RESTEasyPublishException extends SwitchYardException {
    private static final long serialVersionUID = 1L;

    /**
     * Public constructor.
     * @param message Exception message.
     */
    public RESTEasyPublishException(final String message) {
        super(message);
    }

    /**
     * Public constructor.
     * @param message Exception message.
     * @param cause Exception cause.
     */
    public RESTEasyPublishException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Public constructor.
     * @param cause Exception cause.
     */
    public RESTEasyPublishException(final Throwable cause) {
        super(cause);
    }
}
