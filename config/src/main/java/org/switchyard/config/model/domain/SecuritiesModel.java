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
package org.switchyard.config.model.domain;

import java.util.List;

import org.switchyard.config.model.Model;

/**
 * The "securities" configuration model.
 */
public interface SecuritiesModel extends Model {

    /** The "securities" name. */
    public static final String SECURITIES = "securities";

    /**
     * Gets the parent domain model.
     * @return the parent domain model
     */
    public DomainModel getDomain();

    /**
     * Gets the child security models.
     * @return the child security models
     */
    public List<SecurityModel> getSecurities();

}
