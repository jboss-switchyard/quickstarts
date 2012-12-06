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
package org.switchyard.component.camel.sftp.model;

import org.switchyard.component.camel.common.model.remote.CamelRemoteFileBindingModel;

/**
 * Sftp endpoint binding.
 * 
 * @author Lukasz Dywicki
 */
public interface CamelSftpBindingModel extends CamelRemoteFileBindingModel {

    /**
     * The known hosts file.
     * 
     * @return File used to host key signature verification.
     */
    String getKnownHostsFile();

    /**
     * Sets the known_hosts file, so that the SFTP endpoint can do host key verification.
     * 
     * @param knownHostsFile File with known hosts.
     * @return a reference to this binding model
     */
    CamelSftpBindingModel setKnownHostsFile(String knownHostsFile);

    /**
     * Endpoint private key used for authorization.
     * 
     * @return Private key file.
     */
    String getPrivateKeyFile();

    /**
     * Set the private key file to that the SFTP endpoint can do private key verification.
     * 
     * @param privateKeyFile Private key file.
     * @return a reference to this binding model
     */
    CamelSftpBindingModel setPrivateKeyFile(String privateKeyFile);

    /**
     * Passphrase used for private key.
     * 
     * @return Private key passphrase.
     */
    String getPrivateKeyFilePassphrase();

    /**
     * Set the private key file passphrase to that the SFTP endpoint can do private key verification.
     * 
     * @param passphrase Passphrase for key file.
     * @return a reference to this binding model
     */
    CamelSftpBindingModel setPrivateKeyFilePassphrase(String passphrase);

}
