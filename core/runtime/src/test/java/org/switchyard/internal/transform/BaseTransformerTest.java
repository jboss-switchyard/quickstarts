/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.internal.transform;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.transform.BaseTransformer;

public class BaseTransformerTest {

    @Test
    public void testGetType() {
        BaseTransformer<String, Integer> intToStr = 
            new BaseTransformer<String, Integer>() {
                public Integer transform(String from) {
                    return null;
                }
        };

        Assert.assertEquals(String.class, intToStr.getFromType());
        Assert.assertEquals(Integer.class, intToStr.getToType());
    }
    
    @Test
    public void testGetName() {
        final QName fromName = new QName("string1");
        final QName toName = new QName("string2");
        
        BaseTransformer<String, String> intToStr = 
            new BaseTransformer<String, String>(fromName, toName) {
                public String transform(String from) {
                    return null;
                }
        };

        Assert.assertEquals(fromName, intToStr.getFrom());
        Assert.assertEquals(toName, intToStr.getTo());
    }
}
