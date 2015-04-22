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
package org.switchyard.component.clojure.deploy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.common.type.Classes;
import org.switchyard.common.xml.QNameUtil;
import org.switchyard.component.clojure.config.model.ClojureComponentImplementationModel;
import org.switchyard.component.clojure.config.model.ClojureScriptModel;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.SwitchYardException;

import clojure.lang.Var;

/**
 * An ExchangeHandle that can load and invoke a Clojure script. 
 * 
 * @author Daniel Bevenius
 *
 */
public class ClojureHandler extends BaseServiceHandler {
    
    private final ClojureComponentImplementationModel _implModel;
    private Var _var;

    /**
     * Sole constructor.
     * 
     * @param implModel The configuration model.
     */
    public ClojureHandler(final ClojureComponentImplementationModel implModel) {
        _implModel = implModel;
    }
    
    /**
     * Loads the Clojure script.
     */
    @Override
    protected void doStart() {
        try {
            final ClojureScriptModel scriptModel = _implModel.getScriptModel();
            _var = scriptModel != null 
                    ? (Var) clojure.lang.Compiler.load(new StringReader(scriptModel.getScript())) 
                    : (Var) clojure.lang.Compiler.load(loadInputStream(_implModel.getScriptFile()));
        } catch (final Exception e) {
            throw new SwitchYardException(e);
        }
    }
    
    @Override
    public void stop() {
        // Nothing to do here
        // leave state alone
    }
    
    private InputStreamReader loadInputStream(final String scriptFile) throws IOException {
        final InputStream in = Classes.getResourceAsStream(scriptFile);
        if (in != null) {
            return new InputStreamReader(in);
        } else {
            return new InputStreamReader(new FileInputStream(scriptFile));
        }
    }

    @Override
    public void handleMessage(final Exchange exchange) throws HandlerException {
        try {
            Object response =_implModel.injectExchange() 
                    ? _var.invoke(exchange)
                    : _var.invoke(exchange.getMessage().getContent());
            if (response != null) {
                Message message = exchange.createMessage().setContent(response);
                exchange.send(message);
            }
                
        } catch (final Exception e) {
            QName declaredFault = exchange.getContract().getProviderOperation().getFaultType();
            if (declaredFault != null && QNameUtil.isJavaMessageType(declaredFault)
                    && QNameUtil.toJavaMessageType(declaredFault).isAssignableFrom(e.getClass())) {
                Message msg = exchange.createMessage().setContent(e);
                exchange.sendFault(msg);
            } else {
                throw new HandlerException(e);
            }
        }
    }

    @Override
    public void handleFault(final Exchange exchange) {
    }

}
