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
