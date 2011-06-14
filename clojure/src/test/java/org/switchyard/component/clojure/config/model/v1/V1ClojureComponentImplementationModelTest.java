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
package org.switchyard.component.clojure.config.model.v1;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.switchyard.common.type.Classes;
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
        final V1ClojureComponentImplementationModel implModel = new V1ClojureComponentImplementationModel();
        implModel.setInjectExchange(true);
        final V1ClojureScriptModel scriptModel = new V1ClojureScriptModel();
        scriptModel.setScript("bogus script");
        implModel.setScriptModel(scriptModel);
        
        assertThat(implModel.getScriptModel().getScript(), is(equalTo("bogus script")));
        assertThat(implModel.injectExchange(), is(true));
    }
    
    @Test
    public void programmaticCreationWithScriptFile() {
        final V1ClojureComponentImplementationModel implModel = new V1ClojureComponentImplementationModel();
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
