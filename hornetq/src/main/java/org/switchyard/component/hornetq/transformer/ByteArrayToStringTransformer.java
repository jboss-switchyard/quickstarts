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
package org.switchyard.component.hornetq.transformer;

import javax.xml.namespace.QName;

import org.switchyard.transform.BaseTransformer;

/**
 * SwitchYard transformer that takes care of transforming from a byte array
 * to a String.
 * 
 * @author Daniel Bevenius
 *
 */
public class ByteArrayToStringTransformer extends BaseTransformer<byte[],String>{
    
    /**
     * No-args constructor.
     */
    public ByteArrayToStringTransformer() {
        super(new QName("java:byte[]"), new QName("java:java.lang.String"));
    }
    
    @Override
    public String transform(final byte[] from) {
        if (from == null) {
            return null;
        }
        return new String(from);
    }

}
