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
package org.switchyard.component.camel.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class QueryStringTest {

    @Test
    public void testEmptyQueryString() {
        QueryString qs = new QueryString();
        assertEquals("", qs.toString());
    }

    @Test
    public void testQueryStringSingleParam() {
        final String queryString = "?foo=bar";
        QueryString qs = new QueryString().add("foo", "bar");
        assertEquals(queryString, qs.toString());
    }

    @Test
    public void testNullParamIgnored() {
        final String queryString = "?foo=bar";
        QueryString qs = new QueryString()
            .add("foo", "bar")
            .add(null, "abc")
            .add("xyz", null);
        assertEquals(queryString, qs.toString());
    }

    @Test
    public void testQueryStringMultiParam() {
        final String queryString = "?foo=bar&bar=foo";
        QueryString qs = new QueryString()
            .add("foo", "bar")
            .add("bar", "foo");
        assertEquals(queryString, qs.toString());
    }

}
