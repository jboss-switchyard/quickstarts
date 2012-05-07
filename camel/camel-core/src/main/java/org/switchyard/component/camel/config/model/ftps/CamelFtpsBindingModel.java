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
package org.switchyard.component.camel.config.model.ftps;

import org.switchyard.component.camel.config.model.ftp.CamelFtpBindingModel;

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
    CamelFtpBindingModel setSecurityProtocol(String protocol);

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
    CamelFtpBindingModel setImplict(Boolean implicit);

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
    CamelFtpBindingModel setExecPbsz(Long pbsz);

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
    CamelFtpBindingModel setExecProt(String prot);

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
    CamelFtpBindingModel setDisableSecureDataChannelDefaults(Boolean disableSecureDataChannelDefaults);

}
