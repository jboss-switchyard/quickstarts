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
package org.switchyard.component.clojure.config.model.v1;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.switchyard.common.type.Classes;
import org.switchyard.component.clojure.config.model.ClojureNamespace;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.Validation;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Unit test for {@link V1ClojureComponentImplementationModel}.
 * 
 * @author Daniel Bevenius
 *
 */
public class V1ClojureComponentImplementationModelTest {
    
    @Test
    public void inlineScript() throws Exception {
        final V1ClojureComponentImplementationModel implModel = getImplModel("switchyard-clojure-impl.xml");
        final Validation validateModel = implModel.validateModel();
       
        assertThat(validateModel.isValid(), is(true));
        final String script = implModel.getScriptModel().getScript();
        assertThat(script, is(equalTo("(ns printer)(defn print-string [arg] (println arg))")));
    }
    
    @Test
    public void externalFileScript() throws Exception {
        final V1ClojureComponentImplementationModel implModel = getImplModel("switchyard-clojure-impl-file.xml");
        final Validation validateModel = implModel.validateModel();
       
        assertThat(validateModel.isValid(), is(true));
        assertThat(implModel.injectExchange(), is(true));
        assertThat(implModel.getScriptFile(), is(equalTo("sample.clj")));
        assertThat(Classes.getResourceAsStream(implModel.getScriptFile(), getClass()), is(notNullValue()));
    }
    
    @Test
    public void programmaticCreationWithInlineScript() {
        final V1ClojureComponentImplementationModel implModel = new V1ClojureComponentImplementationModel(ClojureNamespace.DEFAULT.uri());
        implModel.setInjectExchange(true);
        final V1ClojureScriptModel scriptModel = new V1ClojureScriptModel(ClojureNamespace.DEFAULT.uri());
        scriptModel.setScript("bogus script");
        implModel.setScriptModel(scriptModel);
        
        assertThat(implModel.getScriptModel().getScript(), is(equalTo("bogus script")));
        assertThat(implModel.injectExchange(), is(true));
    }
    
    @Test
    public void programmaticCreationWithScriptFile() {
        final V1ClojureComponentImplementationModel implModel = new V1ClojureComponentImplementationModel(ClojureNamespace.DEFAULT.uri());
        implModel.setScriptFile("bogusScript.clj");
        
        assertThat(implModel.getScriptFile(), is(equalTo("bogusScript.clj")));
        assertThat(implModel.injectExchange(), is(false));
    }
    
    private V1ClojureComponentImplementationModel getImplModel(final String config) throws Exception {
        final SwitchYardModel model = new ModelPuller<SwitchYardModel>().pull(config, getClass());
        final ComponentModel componentModel = model.getComposite().getComponents().get(0);
        final ComponentImplementationModel implementation = componentModel.getImplementation();
        return (V1ClojureComponentImplementationModel) implementation;
    }

}
