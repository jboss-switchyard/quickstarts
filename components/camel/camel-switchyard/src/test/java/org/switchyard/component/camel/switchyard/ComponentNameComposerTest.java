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
package org.switchyard.component.camel.switchyard;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.Test;
import org.switchyard.component.camel.switchyard.ComponentNameComposer;

/**
 * Tests for {@link org.switchyard.component.camel.switchyard.ComponentNameComposer}.
 * 
 * @author Daniel Bevenius
 */
public class ComponentNameComposerTest {

    @Test
    public void getQueryParamMap() throws Exception {
        final Map<String, String> map = getQueryParamMap("switchyard://SomeService?foo=bar&bar=baz");
        assertThat(map.size(), is(2));
        assertThat(map.get("foo"), equalTo("bar"));
        assertThat(map.get("bar"), equalTo("baz"));
    }

    @Test
    public void getQueryParamMap_no_params() throws Exception {
        assertThat(getQueryParamMap("switchyard://SomeService?foo").isEmpty(), is(true));
    }
    
    private Map<String, String> getQueryParamMap(final String uri) throws URISyntaxException {
        return ComponentNameComposer.getQueryParamMap(new URI(uri));
    }

}
