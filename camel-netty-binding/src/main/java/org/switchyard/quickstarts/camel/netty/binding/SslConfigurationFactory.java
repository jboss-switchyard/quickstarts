/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
            return JBOSS_HOME + "/standalone/configuration/" + fileName;
        }
        return fileName;
    }
}
