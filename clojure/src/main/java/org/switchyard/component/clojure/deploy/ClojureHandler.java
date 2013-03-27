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
package org.switchyard.component.clojure.deploy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.common.type.Classes;
import org.switchyard.component.clojure.config.model.ClojureComponentImplementationModel;
import org.switchyard.component.clojure.config.model.ClojureScriptModel;
import org.switchyard.deploy.ServiceHandler;
import org.switchyard.exception.SwitchYardException;

import clojure.lang.Var;

/**
 * An ExchangeHandle that can load and invoke a Clojure script. 
 * 
 * @author Daniel Bevenius
 *
 */
public class ClojureHandler implements ServiceHandler {
    
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
    public void start() {
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
            throw new HandlerException(e);
        }
    }

    @Override
    public void handleFault(final Exchange exchange) {
    }

}
