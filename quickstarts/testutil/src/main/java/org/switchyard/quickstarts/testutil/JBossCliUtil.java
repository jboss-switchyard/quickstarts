/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.testutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.jboss.as.cli.CliEvent;
import org.jboss.as.cli.CliEventListener;
import org.jboss.as.cli.CommandContext;
import org.jboss.as.cli.CommandContextFactory;
import org.jboss.as.protocol.StreamUtils;
import org.jboss.logging.Logger;

/**
 * JBoss CLI utility class.
 */
public final class JBossCliUtil {
    private static final Logger _logger = Logger.getLogger(JBossCliUtil.class);
    private static final String HOST_PROP_NAME = "org.switchyard.quickstarts.testutil.cli.host";
    private static final String PORT_PROP_NAME = "org.switchyard.quickstarts.testutil.cli.port";
    private static final String USER_PROP_NAME = "org.switchyard.quickstarts.testutil.cli.user";
    private static final String PASSWORD_PROP_NAME = "org.switchyard.quickstarts.testutil.cli.password";
    private static final String NO_LOCAL_AUTH_PROP_NAME = "org.switchyard.quickstarts.testutil.cli.noLocalAuth";
    private static final String INIT_CONSOLE_PROP_NAME = "org.switchyard.quickstarts.testutil.cli.initConsole";
    private static final String CONNECTION_TIMEOUT_PROP_NAME = "org.switchyard.quickstarts.testutil.cli.connectionTimeout";
    
    private static String HOST = "127.0.0.1";
    private static int PORT = 9999;
    private static String USER = null;
    private static String PASSWORD = null;
    private static boolean NO_LOCAL_AUTH = false;
    private static boolean INIT_CONSOLE = false;
    private static int CONNECTION_TIMEOUT = -1;

    static {
        String val = null;
        if ((val = System.getProperty(HOST_PROP_NAME)) != null) {
            HOST = val;
        }
        if ((val = System.getProperty(PORT_PROP_NAME)) != null) {
            PORT = Integer.parseInt(val);
        }
        if ((val = System.getProperty(USER_PROP_NAME)) != null) {
            USER = val;
        }
        if ((val = System.getProperty(PASSWORD_PROP_NAME)) != null) {
            PASSWORD = val;
        }
        if ((val = System.getProperty(NO_LOCAL_AUTH_PROP_NAME)) != null) {
            NO_LOCAL_AUTH = Boolean.parseBoolean(val);
        }
        if ((val = System.getProperty(INIT_CONSOLE_PROP_NAME)) != null) {
            INIT_CONSOLE = Boolean.parseBoolean(val);
        }
        if ((val = System.getProperty(CONNECTION_TIMEOUT_PROP_NAME)) != null) {
            CONNECTION_TIMEOUT = Integer.parseInt(val);
        }
    }

    /**
     * Executes CLI script file.
     * @param path file path
     * @throws Exception exception
     */
    public static void executeCliScript(String path) throws Exception {
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException(path + " is not a valid CLI script file");
        }

        final CommandContext cmdCtx = CommandContextFactory
                                        .getInstance()
                                        .newCommandContext(
                                                HOST
                                                , PORT
                                                , USER
                                                , PASSWORD != null ? PASSWORD.toCharArray() : null
                                                , NO_LOCAL_AUTH
                                                , INIT_CONSOLE
                                                , CONNECTION_TIMEOUT);
        if (_logger.isDebugEnabled()) {
            cmdCtx.addEventListener(new CliEventListener() {
                @Override
                public void cliEvent(CliEvent event, CommandContext ctx) {
                    _logger.debug("CLI Event: " + event.toString());
                }
            });
        }
        cmdCtx.connectController();
        
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            _logger.info("Executing CLI script file: " + path);
            String line = reader.readLine();
            while (cmdCtx.getExitCode() == 0 && !cmdCtx.isTerminated() && line != null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(">>> " + line.trim());
                }
                cmdCtx.handleSafe(line.trim());
                line = reader.readLine();
            }
        } finally {
            StreamUtils.safeClose(reader);
            cmdCtx.terminateSession();
            _logger.info("CLI session is terminated with exit code '" + cmdCtx.getExitCode() + "'");
        }
    }
}
