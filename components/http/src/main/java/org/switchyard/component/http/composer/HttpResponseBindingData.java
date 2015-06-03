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
 
package org.switchyard.component.http.composer;

/**
 * HTTP response binding data.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> &copy; 2012 Red Hat Inc.
 */
public class HttpResponseBindingData extends HttpBindingData {

    private Integer _status;

    /**
     * Get the HTTP response status.
     * @return HTTP response status
     */
    public Integer getStatus() {
        return _status;
    }

    /**
     * Set the HTTP response status.
     * @param status the response status
     */
    public void setStatus(Integer status) {
        _status = status;
    }
}
