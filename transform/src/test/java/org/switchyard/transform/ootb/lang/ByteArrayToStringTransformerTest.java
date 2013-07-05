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
