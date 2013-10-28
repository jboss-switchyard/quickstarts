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

import java.io.File;
import java.io.IOException;

import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.component.clojure.config.model.ClojureComponentImplementationModel;
import org.switchyard.component.clojure.config.model.ClojureNamespace;
import org.switchyard.component.clojure.config.model.v1.V1ClojureComponentImplementationModel;
import org.switchyard.component.clojure.config.model.v1.V1ClojureScriptModel;

/**
 * Builder that is able to popluate and build a {@link ClojureComponentImplementationModel}. 
 * 
 * @author Daniel Bevenius
 *
 */
public class ClojureModelBuilder {
    
    private String _inlineScript;
    private boolean _emptyInlineScript;
    private String _externalScriptPath;
    private boolean _emptyExternalScriptPath;
    private boolean _injectExchange;
    
    /**
     * No-args constructor.
     * 
     * Defaults to creating a version 1.0 {@link ClojureComponentImplementationModel}.
     */
    public ClojureModelBuilder() {
    }

    /**
     * Will set the injectExchange on the underlying model.
     * 
     * @param inject The value to set.
     * @return {@link ClojureModelBuilder} to support method chaining.
     */
    public ClojureModelBuilder injectExchange(final boolean inject) {
        _injectExchange = inject;
        return this;
    }
    
    /**
     * Sets the Clojure script to inject into the 'script' element.
     * 
     * @param script The script file to inline
     * @return {@link ClojureModelBuilder} to support method chaining.
     * @throws ClojureBuilderException If the file could not be located.
     */
    public ClojureModelBuilder inlineScript(final String script) throws ClojureBuilderException {
        _inlineScript = script;
        return this;
    }
    
    /**
     * Specifies that an empty 'script' element should be created.
     * 
     * @param empty true is an empty 'script' element should be created.
     * @return {@link ClojureModelBuilder} to support method chaining.
     * @throws ClojureBuilderException If the file could not be located.
     */
    public ClojureModelBuilder emptyInlineScript(final boolean empty) throws ClojureBuilderException {
        _emptyInlineScript = empty;
        return this;
    }
    
    /**
     * Set the script file path.
     * 
     * @param scriptPath Path to the external (classpath or filesystem) Clojure script.
     * @return {@link ClojureModelBuilder} to support method chaining.
     */
    public ClojureModelBuilder externalScriptPath(String scriptPath) {
        _externalScriptPath = scriptPath;
        return this;
    }
    
    /**
     * Specifies that an empty 'scriptFile' attribute should be created.
     * 
     * @param empty true is an empty 'scriptFile' attribute should be created.
     * @return {@link ClojureModelBuilder} to support method chaining.
     * @throws ClojureBuilderException If the file could not be located.
     */
    public ClojureModelBuilder emptyExternalScriptPath(final boolean empty) throws ClojureBuilderException {
        _emptyExternalScriptPath = empty;
        return this;
    }

    /**
     * Builds the {@link ClojureComponentImplementationModel}.
     * 
     * @return {@link ClojureComponentImplementationModel} the populated {@link ClojureComponentImplementationModel}.
     * @throws ClojureBuilderException If a correct {@link ClojureComponentImplementationModel} could not be built.
     */
    public ClojureComponentImplementationModel build() throws ClojureBuilderException {
        final V1ClojureComponentImplementationModel implModel = new V1ClojureComponentImplementationModel(ClojureNamespace.DEFAULT.uri());
        
        if (isInlineScript()) {
            try {
                final String clojureScript = _emptyInlineScript ? "" : new StringPuller().pull(new File(_inlineScript));
                final V1ClojureScriptModel scriptModel = new V1ClojureScriptModel(ClojureNamespace.DEFAULT.uri());
                scriptModel.setScript(clojureScript);
                implModel.setScriptModel(scriptModel);
            } catch (final IOException e) {
                throw new ClojureBuilderException(e.getMessage(), e);
            }
        } else if (inExternalScriptPath()) {
            final String path = _emptyExternalScriptPath ? "" : _externalScriptPath;
            implModel.setScriptFile(path);
        } else {
            throw new ClojureBuilderException("None of the available options were configured. Available options: " + this.toString());
        }
        
        implModel.setInjectExchange(_injectExchange);
        return implModel;
    }
    
    private boolean inExternalScriptPath() {
        return _externalScriptPath != null || _emptyExternalScriptPath;
    }

    private boolean isInlineScript() {
        return _inlineScript != null || _emptyInlineScript;
    }
    
    @Override
    public String toString()  {
        final StringBuilder sb = new StringBuilder();
        sb.append("[inlineScript=").append(_inlineScript);
        sb.append(",emptyInlineScript=").append(_emptyInlineScript);
        sb.append(",externalScriptPath=").append(_externalScriptPath);
        sb.append(",emptyExternalScriptPath=").append(_emptyExternalScriptPath);
        sb.append(",injectExchange=").append(_injectExchange);
        sb.append("]");
        return sb.toString();
    }
    
}
