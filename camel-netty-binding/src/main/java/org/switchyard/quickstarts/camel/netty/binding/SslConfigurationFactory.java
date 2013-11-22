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
package org.switchyard.quickstarts.camel.netty.binding;

import java.io.File;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
 * Bean used as factory for SSL-related configuration.
 */
@ApplicationScoped
public class SslConfigurationFactory {

    private static final String JBOSS_HOME = System.getenv("JBOSS_HOME");

    /**
     * Creates password bean.
     * 
     * @return Password used to access keystore.
     */
    @Produces @Named("password")
    public String password() {
        return "changeit";
    }

    /**
     * Creates trust store file object.
     * 
     * @return Trust store file.
     */
    @Produces @Named("trustStore")
    public File trustStore() {
        return new File(getPath("users.jks"));
    }

    /**
     * Creates key store file object.
     * 
     * @return Key store file.
     */
    @Produces @Named("keyStore")
    public File keyStore() {
        return new File(getPath("users.jks"));
    }

    /**
     * Utility method to determine location of configuration files.
     */
    private String getPath(String fileName) {
        if (JBOSS_HOME != null) {
            return JBOSS_HOME + "/quickstarts/camel-netty-binding/" + fileName;
        }
        return fileName;
    }
}
