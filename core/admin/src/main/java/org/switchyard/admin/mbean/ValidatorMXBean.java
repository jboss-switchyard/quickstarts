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
package org.switchyard.admin.mbean;

/**
 * Management interface for a SwitchYard validator.
 */
public interface ValidatorMXBean {

    /**
     * The name of the data type validated by this validator.
     * @return the name of type upon which the validator acts
     */
    public String getName();

    /**
     * The implementation type of this validator.
     * @return the validator type (e.g. java, xml, etc.)
     */
    public String getType();

}
