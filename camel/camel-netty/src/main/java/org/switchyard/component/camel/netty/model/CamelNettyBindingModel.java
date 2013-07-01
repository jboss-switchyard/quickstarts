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

import org.switchyard.component.camel.common.model.CamelBindingModel;

/**
 * Configuration binding for netty.
 * 
 * @author Lukasz Dywicki
 */
public interface CamelNettyBindingModel extends CamelBindingModel {

    /**
     * Gets host name / ip.
     * 
     * @return Host name.
     */
    String getHost();

    /**
     * Sets host name.
     * 
     * @param host Host name.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setHost(String host);

    /**
     * Gets connection port.
     * 
     * @return Port number used to connect remote server.
     */
    Integer getPort();

    /**
     * Sets port to use during connection.
     * 
     * @param port Port number.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setPort(int port);

    /**
     * The TCP/UDP buffer sizes to be used during inbound communication. Size is bytes.
     * 
     * @return Inbound buffer size.
     */
    Long getReceiveBufferSize();

    /**
     * Specify inbound buffer size.
     * 
     * @param receiveBufferSize Buffer size.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setReceiveBufferSize(Long receiveBufferSize);

    /**
     * The TCP/UDP buffer sizes to be used during outbound communication. Size is bytes.
     * 
     * @return Outbound buffer size.
     */
    Long getSendBufferSize();

    /**
     * Specify outbound buffer size.
     * 
     * @param sendBufferSize Buffer size.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setSendBufferSize(Long sendBufferSize);

    /**
     * SSL status - on/off.
     * 
     * @return True if ssl is enabled for endpoint.
     */
    Boolean isSsl();

    /**
     * Setting to specify whether SSL encryption is applied to this endpoint.
     * 
     * @param ssl Should ssl be used.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setSsl(Boolean ssl);

    /**
     * Return name of bean instance used as ssl handler.
     * 
     * @return Custom ssl handler bean name.
     */
    String getSslHandler();

    /**
     * Sets sslHandler bean name..
     * 
     * @param sslHandler Name of bean used as ssl handler.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setSslHandler(String sslHandler);

    /**
     * Password bean used to access keystore.
     * 
     * @return Keystore password bean reference.
     */
    String getPassphrase();

    /**
     * Specify keystore password.
     * 
     * @param passphrase Password.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setPassphrase(String passphrase);

    /**
     * Returns security provider name. By default SunX509 is used.
     * 
     * @return Security provider name.
     */
    String getSecurityProvider();

    /**
     * Specify security provider name.
     * 
     * @param securityProvider Name of JSSE provider.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setSecurityProvider(String securityProvider);

    /**
     * Returns keystore format. By default JKS is used.
     * 
     * @return Keystore format.
     */
    String getKeyStoreFormat();

    /**
     * Specify keystore format.
     * 
     * @param keyStoreFormat Format of the keystore.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setKeyStoreFormat(String keyStoreFormat);

    /**
     * Returns name of bean pointing to keystore file.
     * 
     * @return Kestore bean name.
     */
    String getKeyStoreFile();

    /**
     * Specify keystore file name.
     * 
     * @param keyStoreFile Keystore file bean name.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setKeyStoreFile(String keyStoreFile);

    /**
     * Returns name of bean pointing to truststore file.
     * 
     * @return Truststore  bean name.
     */
    String getTrustStoreFile();

    /**
     * Specify truststore file name.
     * 
     * @param trustStoreFile Name of truststore bean.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setTrustStoreFile(String trustStoreFile);

    /**
     * Gets bean reference name representing SSLContextParameters.
     * 
     * @return Bean reference name.
     */
    String getSslContextParametersRef();

    /**
     * Specify SSL context parameters reference name.
     * 
     * @param sslContextParametersRef Name of bean used to retrieve all SSL related settings.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setSslContextParametersRef(String sslContextParametersRef);

    /**
     * Socket multiplexing.
     * 
     * @return True if multiplexing is turned on.
     */
    Boolean isReuseAddress();

    /**
     * Setting to facilitate socket multiplexing.
     * 
     * @param reuseAddress Reuse address.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setReuseAddress(Boolean reuseAddress);

    /**
     * Encoders list containing ChannelDownStreamHandler implementations.
     * 
     * @return Bean name in registry
     */
    String getEncoders();

    /**
     * A list of encoder to be used. You can use a String which have values separated by comma,
     * and have the values be looked up in the Registry. Just remember to prefix the value with
     * # so Camel knows it should look.
     * 
     * @param encoders Name of list in registry.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setEncoders(String encoders);

    /**
     * Encoders list containing ChannelUpStreamHandler implementations.
     * 
     * @return Bean name in registry.
     */
    String getDecoders();

    /**
     * A list of decorder to be used. You can use a String which have values separated by comma,
     * and have the values be looked up in the Registry. Just remember to prefix the value with
     * # so Camel knows it should lookup.
     * 
     * @param decoders Name of list in registry.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setDecoders(String decoders);

    /**
     * Should default codec chanin be used?
     * 
     * @return True to let netty rely on defaults.
     */
    Boolean isAllowDefaultCodec();

    /**
     * The netty component installs a default codec if both, encoder/deocder is null and textline is false.
     * Setting allowDefaultCodec to false prevents the netty component from installing a default codec
     * as the first element in the filter chain.
     * 
     * @param allowDefaultCodec Turn on or off default encoder/decoder chain.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setAllowDefaultCodec(Boolean allowDefaultCodec);

    /**
     * Number of workers to run.
     * 
     * @return Number of workers.
     */
    Integer getWorkerCount();

    /**
     * When netty works on nio mode, it uses default workerCount parameter from Netty, which is cpu_core_threads*2.
     * User can use this operation to override the default workerCount from Netty.
     * 
     * @param workerCount Number of workers.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setWorkerCount(Integer workerCount);

    /**
     * Flag to identify in-out or in only endpoint.
     * 
     * @return True if endpoint is in-out.
     */
    Boolean isSync();

    /**
     * Setting to set endpoint as one-way or request-response.
     * 
     * @param sync Should communication be bidirectional or not?
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setSync(Boolean sync);

    /**
     * Disconnect after operation.
     * 
     * @return True if connection should be closed after use.
     */
    Boolean isDisconnect();

    /**
     * Whether or not to disconnect(close) from Netty Channel right after use.
     * 
     * @param disconnect Close connection after operation.
     * @return a reference to this binding model
     */
    CamelNettyBindingModel setDisconnect(Boolean disconnect);

}
