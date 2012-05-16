package org.switchyard.tools.forge;

import java.io.IOException;
import java.io.OutputStream;

import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.shell.buffers.JLineScreenBuffer;
import org.jboss.forge.shell.console.jline.Terminal;
import org.jboss.forge.shell.console.jline.TerminalFactory;
import org.jboss.forge.shell.integration.BufferManager;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Before;
import org.switchyard.common.version.Versions;

/**
 * Generic Test Class to be used for Forge testing.
 *
 * @author Mario Antollini
 */
public abstract class GenericTestForge extends AbstractShellTest {

    private static String switchyardVersion;

    private static OutputStream outputStream;

    private static String switchyardVersionSuccessMsg;
    
    private static final String FORGE_APP_NAME = "ForgeTestApp";

    /**
     * Constructor.
     */
    public GenericTestForge() {
        switchyardVersion = Versions.getSwitchYardVersion();
        switchyardVersionSuccessMsg = "SwitchYard version " + switchyardVersion;
    }

    /**
     * Let's setup the environment for the test.
     * @throws IOException exception will be caught and printed out
     */
    @Before
    public void prepareSwitchyardForge() throws IOException {
        try {
            resetOutputStream();
            initializeJavaProject();
            queueInputLines(FORGE_APP_NAME);
            getShell().execute("project install-facet switchyard");
            getShell().execute("switchyard get-version");
            Assert.assertTrue(outputStream.toString().contains(switchyardVersionSuccessMsg));
            System.out.println(outputStream);
            resetOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(outputStream);
        }
    }

    /**
     * Handy method for building the Forge project skipping tests.
     */
    protected void mavenBuildSkipTest() {
        String[] mvnCommand = new String[]{"package", "-e", "-Dmaven.test.skip=true"};
        getProject().getFacet(MavenCoreFacet.class).executeMaven(mvnCommand);
    }

    /**
     * Handy method for building the Forge project.
     */
    protected void build() {
        try {
            getShell().execute("build");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * This method provides a way to reset the output stream used by the shell to
     * print its output. As a rule of thumb, this method should be invoked after:
     * 1 - Each explicit system.out operation in order to avoid duplicate lines to
     * be printed in the console and,
     * 2 - Before invoking forge operations which will subsequently be asserted in order
     * to avoid the assertion to wrongly match previous similar output lines
     */
    public void resetOutputStream() {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        outputStream = new OutputStream() {
            private StringBuilder _stringBuilder = new StringBuilder();
            @Override
            public void write(int b) throws IOException {
                _stringBuilder.append((char) b);
            }
            public String toString() {
                return _stringBuilder.toString();
            }
        };
        
        TerminalFactory.configure(TerminalFactory.Type.AUTO);
        Terminal terminal = TerminalFactory.get();
        BufferManager screenBuffer = new JLineScreenBuffer(terminal, outputStream);
        getShell().registerBufferManager(screenBuffer); 
    }

    /**
     * Self-explanatory accessor method.
     * @return the Switchyard version
     */
    public static String getSwitchyardVersion() {
        return switchyardVersion;
    }

    /**
     * Returns the output of the shell being currently executed.
     * @return all the text that was printed in the project's shell after the 
     * latest reset output stream reset {@link #resetOutputStream()} 
     */
    public static String getOutput() {
        return outputStream.toString();
    }


}
