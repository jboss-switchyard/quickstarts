/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.quickstarts.camel.ftp.binding;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
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
    mixins = {CDIMixIn.class}
)
@RunWith(SwitchYardRunner.class)
public class CamelFtpBindingTest {

    private static final String FILE_NAME = "Kris.txt";
    private SwitchYardTestKit _testKit;
    private static FtpServer ftpServer;

    @BeforeClass
    public static void startUp() throws FtpException {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(2222);
        serverFactory.addListener("default", listenerFactory.createListener());
        PropertiesUserManagerFactory managerFactory = new PropertiesUserManagerFactory();
        managerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor());
        managerFactory.setFile(new File("src/test/resources/users.properties"));
        UserManager createUserManager = managerFactory.createUserManager();
        serverFactory.setUserManager(createUserManager);

        NativeFileSystemFactory fileSystemFactory = new NativeFileSystemFactory();
        fileSystemFactory.setCreateHome(true);
        serverFactory.setFileSystem(fileSystemFactory);

        ftpServer = serverFactory.createServer();
        ftpServer.start();
    }

    @AfterClass
    public static void shutDown() {
        ftpServer.stop();
    }

    @Test
    public void receiveFile() throws Exception {
        final String payload = "dummy payload";
        // replace existing implementation for testing purposes
        _testKit.removeService("GreetingService");
        final MockHandler greetingService = _testKit.registerInOnlyService("GreetingService");

        createFile(payload, FILE_NAME);
        // Allow for the JMS Message to be processed.
        Thread.sleep(3000);
        
        final LinkedBlockingQueue<Exchange> recievedMessages = greetingService.getMessages();
        assertThat(recievedMessages, is(notNullValue()));
        final Exchange recievedExchange = recievedMessages.iterator().next();
        assertThat(recievedExchange.getMessage().getContent(String.class), is(equalTo(payload)));
    }
    
    private void createFile(final String text, final String fileName) throws Exception {
        File file = new File("target/ftp", fileName);
        FileUtils.write(file, text);
    }
}
