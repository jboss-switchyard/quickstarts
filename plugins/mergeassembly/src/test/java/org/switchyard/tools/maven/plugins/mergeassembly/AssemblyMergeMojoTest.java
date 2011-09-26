/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.switchyard.tools.maven.plugins.mergeassembly;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class AssemblyMergeMojoTest {

    @Test
    public void test_merge() throws MojoExecutionException, MojoFailureException, IOException, SAXException {
        AssemblyMergeMojo mojo = new AssemblyMergeMojo();

        mojo.setBaseAssembly(new File("src/test/resources/assembly.xml"));

        File finalAssembly = new File("target/assembly.xml");
        mojo.setFinalAssembly(finalAssembly);
        mojo.execute();

        // Check the finalAssembly against the expected...
        Reader expectedXMLReader = new FileReader(new File("src/test/resources/expected-merged-assembly.xml"));
        try {
            Reader actualXMLReader = new FileReader(finalAssembly);
            try {
                XMLUnit.setIgnoreWhitespace(true);
                XMLAssert.assertXMLEqual(expectedXMLReader, actualXMLReader);
            } finally {
                actualXMLReader.close();
            }
        } finally {
            expectedXMLReader.close();
        }
    }
}
