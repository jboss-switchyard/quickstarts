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
package org.switchyard.quickstarts.camel.ftp.binding;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.sftp.subsystem.SftpSubsystem;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;

@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = { CDIMixIn.class })
@RunWith(SwitchYardRunner.class)
public class CamelFtpBindingTest {

    private static final String FILE_NAME = "Kris.txt";
    private SwitchYardTestKit _testKit;
    private static FtpServer ftpServer;
    private static SshServer sshd;

    @BeforeClass
    public static void startUp() throws Exception {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(2222);
        serverFactory.addListener("default", listenerFactory.createListener());

        ListenerFactory sslListenerFactory = new ListenerFactory();
        sslListenerFactory.setPort(2221);
        SslConfigurationFactory ssl = new SslConfigurationFactory();
        ssl.setKeystoreFile(new File("src/test/resources/ftpserver.jks"));
        ssl.setKeystorePassword("password");
        sslListenerFactory.setSslConfiguration(ssl.createSslConfiguration());
        sslListenerFactory.setImplicitSsl(false); // Setting it to true will not read the file
        serverFactory.addListener("ftps", sslListenerFactory.createListener());

        PropertiesUserManagerFactory managerFactory = new PropertiesUserManagerFactory();
        managerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor());
        managerFactory.setFile(new File("src/test/resources/users.properties"));
        UserManager createUserManager = managerFactory.createUserManager();
        serverFactory.setUserManager(createUserManager);

        NativeFileSystemFactory fileSystemFactory = new NativeFileSystemFactory();
        fileSystemFactory.setCreateHome(true);
        serverFactory.setFileSystem(fileSystemFactory);

        File file = new File("target/ftp/ftps");
        file.mkdirs();
        file = new File("target/ftp/sftp");
        file.mkdirs();

        ftpServer = serverFactory.createServer();
        ftpServer.start();

        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(2220);
        sshd.setKeyPairProvider(createTestKeyPairProvider("src/test/resources/hostkey.pem"));
        sshd.setSubsystemFactories(Arrays.<NamedFactory<Command>>asList(new SftpSubsystem.Factory()));
        sshd.setCommandFactory(new ScpCommandFactory());
        sshd.setPasswordAuthenticator(new BogusPasswordAuthenticator());

        sshd.start();

        JSch sch = new JSch();
        Session session = sch.getSession("camel", "localhost", 2220);
        session.setUserInfo(new SimpleUserInfo("isMyFriend"));
        session.connect();
        ChannelSftp c = (ChannelSftp) session.openChannel("sftp");
        c.connect();
        System.out.println("Home: " + c.getHome());
        c.chmod(777, ".");
        c.chmod(777, "target");
        c.chmod(777, "target/ftp");
        c.chmod(777, "target/ftp/sftp");
        c.disconnect();
        session.disconnect();
    }

    @AfterClass
    public static void shutDown() throws Exception {
        if (ftpServer != null) {
            ftpServer.stop();
        }
        if (sshd != null) {
            sshd.stop();
        }
    }

    @Test
    public void receiveFile() throws Exception {
        File file = new File("target/ftp", FILE_NAME);
        File destFile = new File("target/ftp/done", FILE_NAME);
        FileUtils.write(file, "The Phantom");
        // Allow for the file to be processed.
        for (int i = 0; i < 20; i++) {
            Thread.sleep(500);
            if (!file.exists()) {
                break;
            }
        }
        assertFalse(file.exists());
        assertTrue(destFile.exists());

        file = new File("target/ftp/ftps", FILE_NAME);
        destFile = new File("target/ftp/ftps/done", FILE_NAME);
        FileUtils.write(file, "The Ghost Who Walks");
        // Allow for the file to be processed.
        for (int i = 0; i < 20; i++) {
            Thread.sleep(500);
            if (!file.exists()) {
                break;
            }
        }
        assertFalse(file.exists());
        assertTrue(destFile.exists());

        file = new File("target/ftp/sftp", FILE_NAME);
        destFile = new File("target/ftp/sftp/done", FILE_NAME);
        FileUtils.write(file, "Kit Walker");
        // Allow for the file to be processed.
        for (int i = 0; i < 20; i++) {
            Thread.sleep(500);
            if (!file.exists()) {
                break;
            }
        }
        assertFalse(file.exists());
        assertTrue(destFile.exists());
    }

    private File createFile(final String text, final String fileName) throws Exception {
        File file = new File("target/ftp", fileName);
        FileUtils.write(file, text);
        return file;
    }

    public static FileKeyPairProvider createTestKeyPairProvider(String resource) {
        return new FileKeyPairProvider(new String[] { getFile(resource).toString() });
    }

    private static File getFile(String resource) {
        File f = new File(resource);
        return f;
    }

    public static class BogusPasswordAuthenticator implements PasswordAuthenticator {

        public boolean authenticate(String username, String password, ServerSession session) {
            return ((username != null) && (password != null) && username.equals("camel") && password.equals("isMyFriend"));
        }
    }

    public static class SimpleUserInfo implements UserInfo, UIKeyboardInteractive {

        private final String password;

        public SimpleUserInfo(String password) {
            this.password = password;
        }

        public String getPassphrase() {
            return null;
        }

        public String getPassword() {
            return password;
        }

        public boolean promptPassword(String message) {
            return true;
        }

        public boolean promptPassphrase(String message) {
            return false;
        }

        public boolean promptYesNo(String message) {
            return true;
        }

        public void showMessage(String message) {
        }

        public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
            return new String[] { password };
        }
    }
}
