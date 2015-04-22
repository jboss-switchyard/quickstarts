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
package org.switchyard.component.camel.netty.model;

/**
 * Configuration binding for tcp gateway.
 * 
 * @author Lukasz Dywicki
 */
public interface CamelNettyTcpBindingModel extends CamelNettyBindingModel {

    /**
     * Mode of endpoint - binary or text based.
     * 
     * @return True if text is chosen.
     */
    Boolean isTextline();

    /**
     * If no codec is specified, you can use this flag to indicate a text line based codec;
     * if not specified or the value is false, then Object Serialization is assumed over TCP.
     * 
     * @param textline True to use text based communication.
     * @return a reference to this binding model
     */
    CamelNettyTcpBindingModel setTextline(Boolean textline);

    /**
     * TCP_NO_DELAY Flag for socket.
     * 
     * @return True if flag should be set.
     */
    Boolean isTcpNoDelay();

    /**
     * Setting to improve TCP protocol performance.
     * 
     * @param tcpNoDelay True if you willing to set TCP_NO_DELAY on socket.
     * @return a reference to this binding model
     */
    CamelNettyTcpBindingModel setTcpNoDelay(Boolean tcpNoDelay);

    /**
     * Should socket be keept open?
     * 
     * @return True to keep socket open.
     */
    Boolean isKeepAlive();

    /**
     * Setting to ensure socket is not closed due to inactivity.
     * 
     * @param keepAlive True to keep socket open.
     * @return a reference to this binding model
     */
    CamelNettyTcpBindingModel setKeepAlive(Boolean keepAlive);

}
