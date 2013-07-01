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
package org.switchyard.component.camel.ftps.model;

import org.switchyard.component.camel.ftp.model.CamelFtpBindingModel;

/**
 * Ftps endpoint binding.
 * 
 * @author Lukasz Dywicki
 */
public interface CamelFtpsBindingModel extends CamelFtpBindingModel {

    /**
     * Gets security protocol.
     * 
     * @return Protocol to use.
     */
    String getSecurityProtocol();

    /**
     * Sets the underlying security protocol. The following values are defined: 
     * TLS: Transport Layer Security 
     * SSL: Secure Sockets Layer
     *
     * @param protocol Protocol to use.
     * @return a reference to this binding model
     */
    CamelFtpsBindingModel setSecurityProtocol(String protocol);

    /**
     * Gets security mode.
     * 
     * @return True if security mode is implicit.
     */
    Boolean isImplict();

    /**
     * Sets the security mode(implicit/explicit). Default is explicit (false).
     * 
     * @param implicit Implicit flag.
     * @return a reference to this binding model
     */
    CamelFtpsBindingModel setImplict(Boolean implicit);

    /**
     * Gets buffer size for secure data channel.
     * 
     * @return Buffer size.
     */
    Long getExecPbsz();

    /**
     * This option specifies the buffer size of the secure data channel. If option useSecureDataChannel 
     * has been enabled and this option has not been explicit set, then value 0 is used.
     * 
     * @param pbsz Buffer size for secure data channel.
     * @return a reference to this binding model
     */
    CamelFtpsBindingModel setExecPbsz(Long pbsz);

    /**
     * Gets actual execProt value.
     * 
     * @return Prot value.
     */
    String getExecProt();

    /**
     * Will by default use option P if secure data channel defaults hasn't been disabled. Possible values are: 
     * C: Clear 
     * S: Safe (SSL protocol only) 
     * E: Confidential (SSL protocol only) 
     * P: Private
     * 
     * @param prot Prot mode
     * @return a reference to this binding model
     */
    CamelFtpsBindingModel setExecProt(String prot);

    /**
     * Gets mode of secure channel defaults.
     * 
     * @return True if defaults for PROT and PBSZ are disabled.
     */
    Boolean isDisableSecureDataChannelDefaults();

    /**
     * Whether or not to disable using default values for execPbsz and execProt when using secure data transfer. 
     * You can set this option to true if you want to be in absolute full control what the options execPbsz and execProt
     * should be used.
     * 
     * @param disableSecureDataChannelDefaults Flag to disable defaults.
     * @return a reference to this binding model
     */
    CamelFtpsBindingModel setDisableSecureDataChannelDefaults(Boolean disableSecureDataChannelDefaults);

}
