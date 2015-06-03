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

package org.switchyard.component.common;

import org.switchyard.SwitchYardException;

/**
 * Exchange delivery exception.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DeliveryException extends SwitchYardException {

    /**
     * Public constructor.
     * @param message Exception message.
     */
    public DeliveryException(String message) {
        super(message);
    }

    /**
     * Public constructor.
     * @param message Exception message.
     * @param cause Throwable cause.
     */
    public DeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
