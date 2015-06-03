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
package org.switchyard.component.http.config.model;

/**
 * A NTLM Authentication Model.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public interface NtlmAuthModel extends BasicAuthModel {

    /**
     * Get the NTLM domain.
     * @return domain name
     */
    public String getDomain();

    /**
     * Set the NTLM domain.
     * @param domain the domain name
     * @return this NTLMAuthModel
     */
    public NtlmAuthModel setDomain(String domain);
}
