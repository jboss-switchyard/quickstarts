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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;


/**
 * Utility class to safely access ("pull") Properties from various sources.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class PropertiesPuller extends Puller<Properties> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Properties pull(InputStream stream) throws IOException {
        Properties props = new Properties();
        if (stream != null) {
            props.load(stream);
        }
        return props;
    }

    /**
     * Safely pulls Properties from a Reader.
     * @param reader a Reader of the resource
     * @return the resource, or null if not found
     * @throws IOException if a problem occurred
     */
    public Properties pull(Reader reader) throws IOException {
        Properties props = new Properties();
        if (reader != null) {
            props.load(reader);
        }
        return props;
    }

}
