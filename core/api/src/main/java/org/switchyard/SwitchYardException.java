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
package org.switchyard;

/**
 * SwitchYard base exception class.
 *
 * @author Tom Cunningham <a href="mailto:tcunning@redhat.com">&lt;tcunning@redhat.com&gt;</a> (C) 2011 Red Hat Inc.
 */
public class SwitchYardException extends RuntimeException {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -5932547061290094335L;

    /**
     * Public constructor.
     * @param message Exception message.
     */
    public SwitchYardException(String message) {
        super(message);
    }

    /**
     * Public constructor.
     * @param cause Throwable cause.
     */
    public SwitchYardException(Throwable cause) {
        super(cause);
    }

    /**
     * Public constructor.
     * @param message Exception message.
     * @param cause Throwable cause.
     */
    public SwitchYardException(String message, Throwable cause) {
        super(message, cause);
    }
}
