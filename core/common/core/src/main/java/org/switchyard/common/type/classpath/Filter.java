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

package org.switchyard.common.type.classpath;

/**
 * Classpath resource filter.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public interface Filter {

    /**
     * Classpath resource filter method.
     * @param resourceName The classpath resource file name.  Needs to be converted to
     * a proper class name
     */
    void filter(String resourceName);

    /**
     * Should the scanner continue scanning.
     * @return True of the scanner should continue, otherwise false.
     */
    boolean continueScanning();
}
