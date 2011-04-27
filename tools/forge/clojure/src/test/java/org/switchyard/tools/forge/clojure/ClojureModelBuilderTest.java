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
package org.switchyard.tools.forge.clojure;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.FileWriter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.switchyard.component.clojure.config.model.ClojureComponentImplementationModel;

/**
 * Unit test for {@link ClojureModelBuilder}.
 * 
 * @author Daniel Bevenius
 *
 */
public class ClojureModelBuilderTest {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    
    @Test
    public void injectExchange() throws Exception {
        final ClojureComponentImplementationModel implModel = new ClojureModelBuilder().injectExchange(true).emptyExternalScriptPath(true).build();
        assertThat(implModel.injectExchange(), is(true));
    }
    
    @Test
    public void inlineScript() throws Exception {
        final String script = "dummy script";
        final File scriptFile = tmpFolder.newFile("dummy.clj");
        final FileWriter fileWriter = new FileWriter(scriptFile);
        fileWriter.write(script);
        fileWriter.flush();
        
        final ClojureComponentImplementationModel implModel = new ClojureModelBuilder().inlineScript(scriptFile.getAbsolutePath()).build();
        assertThat(implModel.getScriptModel().getScript(), is(equalTo(script)));
    }
    
    @Test
    public void emptyInlineScriptFile() throws Exception {
        final ClojureComponentImplementationModel implModel = new ClojureModelBuilder().emptyInlineScript(true).build();
        assertThat(implModel.getScriptModel().getScript(), is(equalTo("")));
    }
    
    @Test
    public void externalScript() throws Exception {
        final String scriptFile = "/path/someScript.clj";
        final ClojureComponentImplementationModel implModel = new ClojureModelBuilder().externalScriptPath(scriptFile).build();
        assertThat(implModel.getScriptFile(), is(equalTo(scriptFile)));
    }
    
    @Test
    public void emptyExternalScriptPath() throws Exception {
        final ClojureComponentImplementationModel implModel = new ClojureModelBuilder().emptyExternalScriptPath(true).build();
        assertThat(implModel.getScriptFile(), is(equalTo("")));
    }
    
    @Test
    public void should_throw_exception_no_script_options_were_specified() throws Exception {
        thrown.expect(ClojureBuilderException.class);
        thrown.expectMessage("None of the available options were configured");
        new ClojureModelBuilder().build();
    }

}
