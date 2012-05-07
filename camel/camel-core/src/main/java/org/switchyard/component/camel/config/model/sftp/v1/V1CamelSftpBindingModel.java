package org.switchyard.component.camel.config.model.sftp.v1;

import org.switchyard.component.camel.config.model.QueryString;
import org.switchyard.component.camel.config.model.remote.v1.V1CamelRemoteFileBindingModel;
import org.switchyard.component.camel.config.model.sftp.CamelSftpBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Sftp protocol binding.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelSftpBindingModel extends V1CamelRemoteFileBindingModel
    implements CamelSftpBindingModel {

    /**
     * Sftp protocol/endpoint scheme.
     */
    public static final String SFTP = "sftp";

    private static final String KNOWN_HOSTS_FILE = "knownHostsFile";
    private static final String PRIVATE_KEY_FILE = "privateKeyFile";
    private static final String PRIVATE_KEY_FILE_PASSPHRASE = "privateKeyFilePassphrase";

    /**
     * Create a CamelSftpBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param descriptor The switchyard descriptor instance.
     */
    public V1CamelSftpBindingModel(Configuration config, Descriptor descriptor) {
        super(config, descriptor);
    }

    /**
     * Create CamelSftpBindingModel with given protocol.
     * 
     * @param protocol Protocol/binding to use.
     */
    public V1CamelSftpBindingModel(String protocol) {
        super(protocol);
    }

    @Override
    public String getKnownHostsFile() {
        return getConfig(KNOWN_HOSTS_FILE);
    }

    @Override
    public CamelSftpBindingModel setKnownHostsFile(String knownHostsFile) {
        return setConfig(KNOWN_HOSTS_FILE, knownHostsFile);
    }

    @Override
    public String getPrivateKeyFile() {
        return getConfig(PRIVATE_KEY_FILE);
    }

    @Override
    public CamelSftpBindingModel setPrivateKeyFile(String privateKeyFile) {
        return setConfig(PRIVATE_KEY_FILE, privateKeyFile);
    }

    @Override
    public String getPrivateKeyFilePassphrase() {
        return getConfig(PRIVATE_KEY_FILE_PASSPHRASE);
    }

    @Override
    public CamelSftpBindingModel setPrivateKeyFilePassphrase(String passphrase) {
        return setConfig(PRIVATE_KEY_FILE_PASSPHRASE, passphrase);
    }

    @Override
    protected String getEndpointProtocol() {
        return SFTP;
    }

    @Override
    protected void enrichQueryString(QueryString queryString) {
        // nothing to do there
    }

}
