/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.resteasy.config.model;

import java.util.List;
import java.util.Map;

import org.switchyard.config.model.Model;

/**
 * The "contextParams" configuration model.
 */
public interface ContextParamsModel extends Model {

    /** The "contextParams" name. */
    public static final String CONTEXT_PARAMS = "contextParams";

    /**
     * Gets the child contextParam models.
     * @return the child contextParam models
     */
    public List<ContextParamModel> getContextParams();

    /**
     * Converts this ContextParamsModel to a Map<String, String>.
     * @return the Map<String, String>
     */
    public Map<String,String> toMap();

}
