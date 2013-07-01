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
package org.switchyard.tools.forge.clojure;

/**
 * Exception that indicates that a build exception has occured. 
 * 
 * @author Daniel Bevenius
 *
 */
public class ClojureBuilderException extends Exception {

    private static final long serialVersionUID = -8291668022705426598L;

    /**
     * Constructs a ClojureBuilderException with the message and cause passed in.
     * 
     * @param message The error message.
     * @param cause The cause of the error.
     */
    public ClojureBuilderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a ClojureBuilderException with the message passed in.
     * 
     * @param message The error message.
     */
    public ClojureBuilderException(final String message) {
        super(message);
    }

}
