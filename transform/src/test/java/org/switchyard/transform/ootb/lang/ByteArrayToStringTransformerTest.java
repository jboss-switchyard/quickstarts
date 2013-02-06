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
package org.switchyard.transform.ootb.lang;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import javax.xml.namespace.QName;

import org.junit.Test;

/**
 * Unit test for {@link ByteArrayToStringTransformer}.
 * 
 * @author Daniel Bevenius
 *
 */
public class ByteArrayToStringTransformerTest {

    @Test
    public void transformNullByteArray() {
        final ByteArrayToStringTransformer transformer = new ByteArrayToStringTransformer();
        final String transformed = transformer.transform(null);
        assertThat(transformed, is(nullValue()));
    }
    
    @Test
    public void transformByteArray() {
        final ByteArrayToStringTransformer transformer = new ByteArrayToStringTransformer();
        final String payload = "some message body";
        final String transformed = transformer.transform(payload.getBytes());
        assertThat(transformed, is(equalTo(payload)));
    }
    
    @Test
    public void getFromType() {
        final ByteArrayToStringTransformer transformer = new ByteArrayToStringTransformer();
        final QName fromType = transformer.getFrom();
        assertThat(fromType, is(equalTo(new QName("java:byte[]"))));
    }
    
    @Test
    public void getToType() {
        final ByteArrayToStringTransformer transformer = new ByteArrayToStringTransformer();
        final QName toType = transformer.getTo();
        assertThat(toType, is(equalTo(new QName("java:java.lang.String"))));
    }

}
