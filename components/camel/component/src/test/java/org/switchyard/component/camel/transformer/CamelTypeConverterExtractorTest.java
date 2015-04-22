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
package org.switchyard.component.camel.transformer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.camel.impl.DefaultCamelContext;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.metadata.JavaTypes;
import org.switchyard.transform.internal.TransformerRegistryLoader;

/**
 * Test for {@link CamelTypeConverterExtractor}
 * 
 * @author Daniel Bevenius
 *
 */
public class CamelTypeConverterExtractorTest
{
    private static CamelTypeConverterExtractor _extractor;
    private static BaseTransformerRegistry _transformerRegistry;

    @BeforeClass
    public static void init() throws Exception {
        final DefaultCamelContext camelContext = new DefaultCamelContext();
        _extractor = new CamelTypeConverterExtractor(camelContext);
        _extractor.init();
        
        _transformerRegistry = new BaseTransformerRegistry();
        new TransformerRegistryLoader(_transformerRegistry).loadOOTBTransforms();
    }
    
    @Test
    public void extractTypeConverterTypes() throws Exception {
        final Map<QName, Set<QName>> transformTypes = _extractor.getTransformTypes();
        final Set<QName> toTypes = transformTypes.get(JavaTypes.toMessageType(String.class));
        
        assertThat(toTypes, hasItems(
                new QName("java:java.io.InputStream"),
                new QName("java:java.io.StringReader"),
                new QName("java:java.io.File"),
                new QName("java:java.nio.ByteBuffer"),
                new QName("java:long"),
                new QName("java:char"),
                new QName("java:byte[]"),
                new QName("java:char[]"),
                new QName("java:javax.xml.transform.Source"),
                new QName("java:javax.xml.transform.dom.DOMSource"),
                new QName("java:javax.xml.transform.sax.SAXSource"),
                new QName("java:org.w3c.dom.Document"),
                new QName("java:org.apache.camel.StringSource")));
    }
    
    @Test
    public void generateTransformsModel() {
        final TransformsModel v1TransformsModel = _extractor.getTransformsModel();
        
        assertThat(v1TransformsModel, is(not(nullValue())));
        assertThat(v1TransformsModel.getTransforms().size(), is(greaterThan(161)));
	}
    
    @Ignore ("This method can be enabled to manually genereate a transforms.xml file in the output directory")
    public void generateTransformsModelFile() throws FileNotFoundException, IOException {
        FileOutputStream fileOut = null;
        try {
	        fileOut = new FileOutputStream("target/classes/new-transforms.xml");
	        final TransformsModel transformsModel = _extractor.getTransformsModel(_transformerRegistry);
	        transformsModel.write(fileOut);
        } finally {
            if (fileOut != null) {
                fileOut.close();
            }
        }
   }
    

}
