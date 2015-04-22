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

import org.switchyard.console.client.NameTokens;

import com.google.web.bindery.autobean.shared.AutoBean;

/**
 * QNameCategory
 * 
 * Provides implementations for the non-property methods in HasQName.
 * 
 * @author Rob Cernich
 */
public final class QNameCategory {

    private QNameCategory() {
    }

    /**
     * @param instance the bean instance.
     * @return the "local" part of the bean's name.
     */
    public static String localName(AutoBean<? extends HasQName> instance) {
        String name = instance.as().getName();
        if (name == null) {
            return null;
        }
        return NameTokens.parseQName(name)[1];
    }

    /**
     * @param instance the bean instance.
     * @return the "namespace" part of the bean's name.
     */
    public static String namespace(AutoBean<? extends HasQName> instance) {
        String name = instance.as().getName();
        if (name == null) {
            return null;
        }
        return NameTokens.parseQName(name)[0];
    }
}
