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
package org.switchyard.transform.xslt.internal;

import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.switchyard.common.type.Classes;

/**
 * Allows stylesheet resources to be resolved from the class loader chain using 
 * relative paths in a stylesheet include.
 */
public class XsltUriResolver implements URIResolver {

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        InputStream inputStream = Classes.getClassLoader().getResourceAsStream(href);
        return new StreamSource(inputStream);
    }
}
