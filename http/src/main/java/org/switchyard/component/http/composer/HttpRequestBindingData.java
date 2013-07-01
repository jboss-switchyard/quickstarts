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

import java.util.Collections;
import java.util.Set;

import org.switchyard.component.common.composer.SecurityBindingData;
import org.switchyard.security.credential.Credential;

/**
 * HTTP request binding data.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> &copy; 2012 Red Hat Inc.
 */
public class HttpRequestBindingData extends HttpBindingData implements SecurityBindingData {
    private HttpRequestInfo _requestInfo;

    /**
     * @return the requestInfo
     */
    public HttpRequestInfo getRequestInfo() {
        return _requestInfo;
    }

    /**
     * @param requestInfo the requestInfo to set
     */
    public void setRequestInfo(HttpRequestInfo requestInfo) {
        _requestInfo = requestInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Credential> extractCredentials() {
        return _requestInfo != null ? _requestInfo.getCredentials() : Collections.<Credential>emptySet();
    }

}
