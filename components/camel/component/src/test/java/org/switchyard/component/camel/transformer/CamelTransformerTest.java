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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;

import javax.xml.namespace.QName;

import org.apache.camel.component.file.GenericFile;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.switchyard.metadata.JavaTypes;

/**
 * Unit test for {@link CamelTransformer}.
 * 
 * @author Daniel Bevenius
 *
 */
public class CamelTransformerTest {
    
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    
    @Test
    public void genericFileToString() throws Exception {
        final QName genericFileType = new QName("java:org.apache.camel.component.file.GenericFile");
        final QName stringType = JavaTypes.toMessageType(String.class);
        
        final CamelTransformer camelTransformer = new CamelTransformer();
        camelTransformer.setFrom(genericFileType);
        camelTransformer.setTo(stringType);
        
        final GenericFile<File> genericFile = new GenericFile<File>();
        genericFile.setBody("dummy file content");
        
        final Object transform = camelTransformer.transform(genericFile);
        assertThat(transform, is(instanceOf(String.class)));
    }

}
