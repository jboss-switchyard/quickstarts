/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.resteasy.context;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.jboss.resteasy.plugins.providers.StringTextStar;

@Provider
@Produces(SyTxtMessageBodyRW.MEDIA_TYPE)
public class SyTxtMessageBodyRW extends StringTextStar {

    public static final String MEDIA_TYPE = "sy/txt";

    private static boolean invoked = false;

    @Override
    public void writeTo(String o, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException {

        invoked = true;
        super.writeTo(o, type, genericType, annotations, mediaType, httpHeaders, entityStream);
    }

    public static boolean isInvoked() {
        return invoked;
    }
}
