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
package org.switchyard.console.client.model;

import com.google.web.bindery.autobean.shared.AutoBean;

/**
 * QNameCategory
 * 
 * Provides implementations for the non-property methods in HasQName.
 * 
 * @author Rob Cernich
 */
public final class ArtifactReferenceCategory {

    private ArtifactReferenceCategory() {
    }

    /**
     * @param instance the bean instance.
     * @return a unique identifier for the instance.
     */
    public static String key(AutoBean<? extends ArtifactReference> instance) {
        ArtifactReference ar = instance.as();
        return "" + ar.getName() + "::" + ar.getUrl();
    }

}
