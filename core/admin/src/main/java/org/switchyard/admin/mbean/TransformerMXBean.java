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
 * Management interface for a SwitchYard transformer.
 */
public interface TransformerMXBean {

    /**
     * Transformer "from" type name.
     * @return the "from" type upon which the transformer acts
     */
    public String getFrom();

    /**
     * Transformer "to" type name.
     * @return the "to" type produced by the transformer
     */
    public String getTo();

    /**
     * The implementation type of the transformer.
     * @return the transformer type (e.g. java, smooks, etc.)
     */
    public String getType();

}
