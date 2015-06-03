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
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ResourceExistsFilter implements Filter {

    private String _resourceName;
    private boolean _resourceFound = false;

    /**
     * Public constructor.
     * @param resourceName The name of the resource to be checked for.
     */
    public ResourceExistsFilter(String resourceName) {
        this._resourceName = resourceName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(String resourceName) {
        _resourceFound = resourceName.equals(_resourceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean continueScanning() {
        return !_resourceFound;
    }

    /**
     * Was the resource was found on the scan.
     * @return True if the resource was found, otherwise false.
     */
    public boolean resourceExists() {
        return _resourceFound;
    }
}
