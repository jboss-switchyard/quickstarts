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
package org.switchyard.component.camel.switchyard.util;

import org.switchyard.Context;
import org.switchyard.Scope;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.camel.common.composer.CamelContextMapper;

public class Mapper extends CamelContextMapper {

    public static final String PROPERTY = "TestProperty";
    public static final String VALUE = "TestValue";

    @Override
    public void mapFrom(CamelBindingData source, Context context) throws Exception {
        super.mapFrom(source, context);
        context.setProperty(PROPERTY, VALUE);
    }

    @Override
    public void mapTo(Context context, CamelBindingData target) throws Exception {
        super.mapTo(context, target);
        target.getMessage().setHeader(PROPERTY, VALUE);
    }

}
