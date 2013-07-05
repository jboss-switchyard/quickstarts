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

package org.switchyard.common.io.pull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Utility class to safely access ("pull") Strings from various sources.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class StringPuller extends Puller<String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String pull(InputStream stream) throws IOException {
        return pull(new InputStreamReader(stream));
    }

    /**
     * Safely pulls a String from a Reader.
     * @param reader a Reader of the resource
     * @return the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public String pull(Reader reader) throws IOException {
        StringBuffer buffer = new StringBuffer(1024);
        reader = new BufferedReader(reader);
        char[] c = new char[1024];
        int i = 0;
        while ((i = reader.read(c)) != -1) {
            buffer.append(c, 0, i);
        }
        return buffer.toString();
    }

}
