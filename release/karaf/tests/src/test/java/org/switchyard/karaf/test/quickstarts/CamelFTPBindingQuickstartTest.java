package org.switchyard.karaf.test.quickstarts;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Logger;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import java.io.File;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
//import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.Listener;
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

public class CamelFTPBindingQuickstartTest extends AbstractQuickstartTest {
    private static String bundleName = "org.switchyard.quickstarts.switchyard.camel.ftp.binding";
    private static String featureName = "switchyard-quickstart-camel-ftp-binding";

    private static FtpServer ftpServer;
    private static SshServer sshd;

    @BeforeClass
    public static void before() throws Exception {

        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(2220);
        sshd.setKeyPairProvider(createTestKeyPairProvider("target/test-classes/quickstarts/camel-ftp-binding/hostkey.pem"));
        sshd.setSubsystemFactories(Arrays.<NamedFactory<Command>>asList(new SftpSubsystem.Factory()));
        sshd.setCommandFactory(new ScpCommandFactory());
        sshd.setPasswordAuthenticator(new BogusPasswordAuthenticator());
        //sshd.setFileSystemFactory(new org.apache.sshd.common.file.nativefs.NativeFileSystemFactory());

        sshd.start();

        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(2222);
        serverFactory.addListener("default", listenerFactory.createListener());

        ListenerFactory sslListenerFactory = new ListenerFactory();
        sslListenerFactory.setPort(2221);
        SslConfigurationFactory ssl = new SslConfigurationFactory();
        ssl.setKeystoreFile(getFile("target/test-classes/quickstarts/camel-ftp-binding/ftpserver.jks"));
        ssl.setKeystorePassword("password");
        sslListenerFactory.setSslConfiguration(ssl.createSslConfiguration());
        sslListenerFactory.setImplicitSsl(false); // Setting it to true will not read the file
        serverFactory.addListener("ssl", sslListenerFactory.createListener());

        PropertiesUserManagerFactory managerFactory = new PropertiesUserManagerFactory();
        managerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor());
        managerFactory.setFile(getFile("target/test-classes/quickstarts/camel-ftp-binding/ftp-users.properties"));
        UserManager createUserManager = managerFactory.createUserManager();
        serverFactory.setUserManager(createUserManager);

        // This doesn't work due to class method signature mismatch
        //NativeFileSystemFactory fileSystemFactory = new NativeFileSystemFactory();
        //fileSystemFactory.setCreateHome(true);
        //serverFactory.setFileSystem(fileSystemFactory);

        File file = new File("target/ftp/ftps");
        file.mkdirs();
        file = new File("target/ftp/sftp");
        file.mkdirs();

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

        ftpServer = serverFactory.createServer();
        ftpServer.start();
        startTestContainer(featureName, bundleName);
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
    public void testFeatures() throws Exception {

        // Ftp
        File srcFile = new File("target/ftp", "test.txt");
        FileUtils.write(srcFile, "The Phantom");
        for (int i = 0; i < 20; i++) {
            Thread.sleep(500);
            if (!srcFile.exists()) {
                break;
            }
        }
        // File should have been picked up
        assertFalse(srcFile.exists());
        File destFile = new File("target/ftp/done", "test.txt");
        assertTrue(destFile.exists());

        // Ftps
        srcFile = new File("target/ftp/ftps", "ftps-test.txt");
        FileUtils.write(srcFile, "The Ghost Who Walks");
        for (int i = 0; i < 20; i++) {
            Thread.sleep(500);
            if (!srcFile.exists()) {
                break;
            }
        }
        // File should have been picked up
        assertFalse(srcFile.exists());
        destFile = new File("target/ftp/ftps/done", "ftps-test.txt");
        assertTrue(destFile.exists());

        // Sftp
        srcFile = new File("target/ftp/sftp", "sftp-test.txt");
        FileUtils.write(srcFile, "Christopher Walker");
        for (int i = 0; i < 20; i++) {
            Thread.sleep(500);
            if (!srcFile.exists()) {
                break;
            }
        }
        // File should have been picked up
        assertFalse(srcFile.exists());
        destFile = new File("target/ftp/sftp/done", "sftp-test.txt");
        assertTrue(destFile.exists());

    }

    public static FileKeyPairProvider createTestKeyPairProvider(String resource) {
        return new FileKeyPairProvider(new String[] { getFile("target/test-classes/quickstarts/camel-ftp-binding/hostkey.pem").toString() });
    }

    public static int getFreePort() throws Exception {
        ServerSocket s = new ServerSocket(0);
        try {
            return s.getLocalPort();
        } finally {
            s.close();
        }
    }

    private static File getFile(String resource) {
        /*URL url = CamelFTPBindingQuickstartTest.class.getClassLoader().getResource(resource);
        File f;
        try {
            f = new File(url.toURI());
        } catch(URISyntaxException e) {
            f = new File(url.getPath());
        }
        return f;*/
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
