/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.transformer;

import org.apache.camel.TypeConverter;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.PackageScanClassResolver;
import org.switchyard.component.camel.deploy.CamelActivator;

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

        PackageScanClassResolver packageScanClassResolver = CamelActivator.getPackageScanClassResolver();
        if (packageScanClassResolver != null) {
            camelContext.setPackageScanClassResolver(packageScanClassResolver);
        }

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
