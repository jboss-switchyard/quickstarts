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

import org.switchyard.component.clojure.config.model.ClojureComponentImplementationModel;
import org.switchyard.component.clojure.config.model.ClojureScriptModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1ComponentImplementationModel;

/**
 * Version 1 implementation of a {@link ClojureComponentImplementationModel}. 
 * 
 * @author Daniel Bevenius
 *
 */
public class V1ClojureComponentImplementationModel extends V1ComponentImplementationModel implements ClojureComponentImplementationModel {
    
    private Boolean _injectExchange;
    private String _scriptFile;
    private ClojureScriptModel _scriptModel;
    
    /**
     * Constructor.
     * @param namespace namespace
     */
    public V1ClojureComponentImplementationModel(String namespace) {
        super(CLOJURE, namespace);
    }

    /**
     * Constructor.
     * 
     * @param config The configuration model.
     * @param desc The descriptor for the model.
     */
    public V1ClojureComponentImplementationModel(final Configuration config, final Descriptor desc) {
        super(config, desc);
    }

    @Override
    public ClojureScriptModel getScriptModel() {
        if (_scriptModel != null) {
            return _scriptModel;
        }
        
        _scriptModel = (ClojureScriptModel) getFirstChildModel(SCRIPT);
        return _scriptModel;
    }
    
    @Override
    public V1ClojureComponentImplementationModel setScriptModel(final ClojureScriptModel scriptModel) {
        setChildModel(scriptModel);
        _scriptModel = scriptModel;
        return this;
    }

    @Override
    public String getScriptFile() {
        if (_scriptFile != null) {
            return _scriptFile;
        }
        
        _scriptFile = getModelAttribute(SCRIPT_FILE);
        return _scriptFile;
    }
    
    @Override
    public V1ClojureComponentImplementationModel setScriptFile(final String scriptFile) {
        setModelAttribute(SCRIPT_FILE, scriptFile);
        _scriptFile = scriptFile;
        return this;
    }
    
    @Override
    public Boolean injectExchange() {
        if (_injectExchange != null) {
            return _injectExchange;
        }
        
        _injectExchange = Boolean.valueOf(getModelAttribute(INJECT_EXCHANGE));
        return _injectExchange;
    }

    @Override
    public ClojureComponentImplementationModel setInjectExchange(final Boolean enable)
    {
        setModelAttribute(INJECT_EXCHANGE, enable.toString());
        _injectExchange = enable;
        return this;
    }
}
