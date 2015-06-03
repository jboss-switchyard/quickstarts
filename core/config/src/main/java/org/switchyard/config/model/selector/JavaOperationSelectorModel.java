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
package org.switchyard.config.model.selector;



/**
 * Java OperationSelector model.
 */
public interface JavaOperationSelectorModel extends OperationSelectorModel {

    /** The "java" name. */
    public static final String JAVA = "java";
    
    /** class attribute. */
    public static final String CLASS = "class";
    
    /**
     * Get the fully qualified class name of OperationSelector.
     * @return class name
     */
    String getClazz();
    
    /**
     * Set the fully qualified class name of OperationSelector.
     * @param clazz the fully qualified class name of OperationSelector
     * @return this OperationSelectorModel for chaining
     */
    JavaOperationSelectorModel setClazz(String clazz);
}
