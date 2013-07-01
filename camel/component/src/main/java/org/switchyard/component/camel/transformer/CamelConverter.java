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
package org.switchyard.component.camel.transformer;

import org.apache.camel.TypeConverter;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * A singleton converter that can be used by Switchyard Transformers
 * that need to delegate to Camel's TypeConverters.
 * 
 * @author Daniel Bevenius
 *
 */
public final class CamelConverter {
    
    private static final CamelConverter INSTANCE = new CamelConverter();
    
    private TypeConverter _typeConverter;
    
    private CamelConverter() {
        final DefaultCamelContext camelContext = new DefaultCamelContext();

//        PackageScanClassResolver packageScanClassResolver = CamelActivator.getPackageScanClassResolver();
//        if (packageScanClassResolver != null) {
//            camelContext.setPackageScanClassResolver(packageScanClassResolver);
//        }

        _typeConverter = camelContext.getTypeConverter();
    }
    
    /**
     * Delegates to Camel's TypeConverter.
     * 
     * @param toType the type to convert to
     * @param from the object to convert from
     * @return Object the converted object
     */
    public Object convert(final Class<?> toType, final Object from) {
        return _typeConverter.convertTo(toType, from);
    }
    
    /**
     * Gets the singleton instance.
     * @return CamelConverter the singleton instance.
     */
    public static CamelConverter instance() {
        return INSTANCE;
    }

}
