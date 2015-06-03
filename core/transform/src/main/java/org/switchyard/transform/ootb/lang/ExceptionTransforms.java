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
package org.switchyard.transform.ootb.lang;

import org.switchyard.annotations.Transformer;

/**
 * Exception transforms.
 */
public class ExceptionTransforms {

    /**
     * Singleton Instance.
     */
    public static final ExceptionTransforms TRANSFORMER = new ExceptionTransforms();
    
    private static final String NL = System.getProperty("line.separator");

    /**
     * Transform to String.
     * @param t Exception to extract
     * @return String.
     */
    @Transformer
    public String toString(Throwable t) {
        Throwable cause = t;
        StringBuilder result = new StringBuilder(cause.getClass().getName() + ": " + cause.getMessage());
        while ((cause = cause.getCause()) != null) {
            result.append(NL + " --- Caused by " + cause.getClass().getName() + ": " + cause.getMessage());
        }
        return result.toString();
    }
}
