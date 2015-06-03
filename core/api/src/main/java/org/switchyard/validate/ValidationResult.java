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

package org.switchyard.validate;

/**
 * Represents message validation result which contains error details if it failed.
 */
public interface ValidationResult {
    
    /**
     * Return whether the validation succeeded or not.
     * @return true if it succeeded
     */
    boolean isValid();
    
    /**
     * Return error details if it failed.
     * @return error message
     */
    String getDetail();
}
