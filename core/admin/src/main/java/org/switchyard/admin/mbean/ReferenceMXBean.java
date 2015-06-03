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

import java.util.List;

/**
 * Management interface for a composite reference consumed by a SwitchYard application.
 */
public interface ReferenceMXBean extends MessageMetricsMXBean {

    /**
     * Reference name.
     * @return the name of this reference.
     */
    String getName();

    /**
     * The component reference that this composite reference promotes.
     * @return the component reference promoted by this reference.
     */
    String getPromotedReference();

    /**
     * List of managed bindings used by this reference.
     * @return management interfaces for gateway bindings on this reference.
     */
    List<BindingMXBean> getBindings();

    /**
     * The composite reference interface.
     * @return the interface used by this reference.
     */
    String getInterface();

    /**
     * The application which uses this reference.
     * @return the application which exports this reference.
     */
    ApplicationMXBean getApplication();
}
