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
package org.switchyard.as7.extension;

/**
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 *
 */
public interface CommonAttributes {

    /**
     * The security-configs attribute.
     */
    String SECURITY_CONFIGS = "security-configs";

    /**
     * The security-config attribute.
     */
    String SECURITY_CONFIG = "security-config";

    /**
     * The component modules attribute.
     */
    String MODULES = "modules";

    /**
     * The component module attribute.
     */
    String MODULE = "module";

    /**
     * The component implementation identifier attribute.
     */
    String IDENTIFIER = "identifier";

    /**
     * The component implementation class name attribute.
     */
    String IMPLCLASS = "implClass";

    /**
     * The environment properties attribute.
     */
    String PROPERTIES = "properties";

    /**
     * The socket-binding property value.
     */
    String SOCKET_BINDING = "socket-binding";

    /**
     * The replaceable properties.
     */
    String DOLLAR = "$";

    /**
     * The extensions element.
     */
    String EXTENSIONS = "extensions";

    /**
     * The extension element.
     */
    String EXTENSION = "extension";

}
