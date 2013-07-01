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
