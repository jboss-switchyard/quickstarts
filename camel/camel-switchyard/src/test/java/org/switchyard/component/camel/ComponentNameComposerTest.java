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
package org.switchyard.component.camel;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.Test;

/**
 * Tests for {@link ComponentNameComposer}.
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
