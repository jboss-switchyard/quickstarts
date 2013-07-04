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
