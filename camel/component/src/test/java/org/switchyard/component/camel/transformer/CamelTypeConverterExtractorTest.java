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
import org.switchyard.metadata.java.JavaService;
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
        final Set<QName> toTypes = transformTypes.get(JavaService.toMessageType(String.class));
        
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
        final TransformsModel v1TransformsModel = _extractor.getV1TransformsModel();
        
        assertThat(v1TransformsModel, is(not(nullValue())));
        assertThat(v1TransformsModel.getTransforms().size(), is(greaterThan(161)));
	}
    
    @Ignore ("This method can be enabled to manually genereate a transforms.xml file in the output directory")
    public void generateTransformsModelFile() throws FileNotFoundException, IOException {
        FileOutputStream fileOut = null;
        try {
	        fileOut = new FileOutputStream("target/classes/new-transforms.xml");
	        final TransformsModel v1TransformsModel = _extractor.getV1TransformsModel(_transformerRegistry);
	        v1TransformsModel.write(fileOut);
        } finally {
            if (fileOut != null) {
                fileOut.close();
            }
        }
   }
    

}
