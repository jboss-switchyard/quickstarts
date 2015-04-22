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

package org.switchyard.transform.config.model;

/**
 * Smooks transformation type.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public enum SmooksTransformType {
    /**
     * Basic Smooks Transformation.
     * <p/>
     * The result type is defined through the &lt;core:exports&gt;
     * section of the configuration.
     * <p/>
     * See the <a href="www.smooks.org">Smooks User Guide</a>.
     */
    SMOOKS,
    /**
     * XML to Java Transformation via Smooks Java Binding configurations.
     * <p/>
     * See the <a href="www.smooks.org">Smooks User Guide</a>.
     */
    XML2JAVA,
    /**
     * Java to XML Transformation via Smooks Java Binding configurations.
     * <p/>
     * See the <a href="www.smooks.org">Smooks User Guide</a>.
     */
    JAVA2XML
}
