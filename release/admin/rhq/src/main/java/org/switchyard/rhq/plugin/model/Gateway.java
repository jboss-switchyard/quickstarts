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
package org.switchyard.rhq.plugin.model;

import javax.xml.namespace.QName;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * SwitchYard Gateway (Binding)
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Gateway implements NamedResource {
    private final QName name;
    private final String type;
    private final String configuration;
    private final String state;
    
    @JsonCreator
    public Gateway(
            @JsonProperty("name") QName name,
            @JsonProperty("type") final String type,
            @JsonProperty("configuration") String configuration,
            @JsonProperty("state") String state) {
        this.name = name;
        this.type = type;
        this.configuration = configuration;
        this.state = state;
    }

    public QName getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getConfiguration() {
        return configuration;
    }

    public String getState() {
        return state;
    }
}
