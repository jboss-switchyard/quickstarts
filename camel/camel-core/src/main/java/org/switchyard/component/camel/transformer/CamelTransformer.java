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

import javax.xml.namespace.QName;

import org.switchyard.common.xml.QNameUtil;
import org.switchyard.transform.BaseTransformer;

/**
 * A SwitchYard transformer that delegates to Apache Camel's converter framework.
 * </p> 
 * 
 * This gives SwitchYard components that use the SwitchYard Camel component to have 
 * access to all converters that are provided by Camel and at the same time enables 
 * the from and to types to be explicitely declared on the SwitchYard contracts.  
 * 
 * @author Daniel Bevenius
 *
 */
public class CamelTransformer extends BaseTransformer {
    
    private CamelConverter _camelConverter = CamelConverter.instance();
    
    /**
     * No-args constructor.
     */
    public CamelTransformer() {
    }
    
    /**
     * 
     * @param from The QName of the type that this transformer is capable of transforming from
     * @param to The QName of the type that this transformer is capable of transforming to
     */
    public CamelTransformer(final QName from, final QName to) {
        super(from, to);
    }

     @Override
     public Object transform(final Object from) {
         if (from == null) {
             return null;
         }
         
         final Class<?> toType = QNameUtil.toJavaMessageType(getTo());
         return _camelConverter.convert(toType, from);
     }
     
}
