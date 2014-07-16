package org.switchyard.karaf.test.quickstarts;

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Test;
import org.ops4j.pax.exam.ExamSystem;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestAddress;
import org.ops4j.pax.exam.TestContainer;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.TestProbeProvider;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.spi.PaxExamRuntime;

public abstract class AbstractQuickstartTest {
    protected static ExamSystem system;
    protected static TestContainer testContainer;
    private static TestProbeProvider testProbe;

    @AfterClass
    public static void after() throws Exception {
        if (testContainer != null) {
            final TestContainer tc = testContainer;
            testContainer = null;
            tc.stop();
        }
    }

    @Test
    public void testDeployment() throws Exception {
        /*
         * Do nothing. Deployment is tested when the test container is started.
         * This method simply prevents errors for "No runnable methods" for
         * those test cases that don't actually exercise the deployment.
         */
    }

    protected static void startTestContainer(String featureName, String bundleName) throws Exception {
        startTestContainer(featureName, bundleName, null, DeploymentProbe.class);
    }

    protected static void startTestContainer(String featureName, String bundleName, Option[] additionalOptions, Class<? extends DeploymentProbe> probeClass) throws Exception {
        system = PaxExamRuntime.createTestSystem(mergeOptions(config(featureName, bundleName), additionalOptions));
        testContainer = PaxExamRuntime.createContainer(system);
        testContainer.start();
        try {
            // install the probe
            TestProbeBuilder probeBuilder = system.createProbe();
            probeBuilder.addTests(probeClass, probeClass.getMethods());
            testProbe = probeBuilder.build();
            testContainer.installProbe(testProbe.getStream());
            // wait for SwitchYard application activation
            executeProbe("testBundleActivation");
        } catch (Exception e) {
            // cleanup
            after();
            throw e;
        }
    }

    protected static void executeProbe(String operation) throws Exception {
        final String testOperation = "." + operation;
        for (TestAddress test : testProbe.getTests()) {
            if (test.caption().endsWith(testOperation)) {
                testContainer.call(test);
                break;
            }
        }
    }

    private static Option[] config(String featureName, String bundleName) throws Exception {
        return options(
                // karafDistributionConfiguration().frameworkUrl(maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("tar.gz").versionAsInProject())
                karafDistributionConfiguration()
                        .frameworkUrl(new File("target/switchyard-karaf-tests-bin.tar.gz").toURI().toURL().toString())
                        .karafVersion(MavenUtils.getArtifactVersion("org.apache.karaf", "apache-karaf"))
                        .name("Apache Karaf").unpackDirectory(new File("target/karaf/" + featureName))
                        .useDeployFolder(false),
                //keepRuntimeFolder(), // this could leave behind lots of cruft
                logLevel(LogLevel.INFO),
                configureConsole().ignoreLocalConsole().ignoreRemoteShell(),
                editConfigurationFileExtend("etc/config.properties", "org.osgi.framework.system.packages.extra",
                        "sun.misc"),
                editConfigurationFilePut("etc/system.properties", "org.switchyard.component.http.standalone.port",
                        "8181"),
                editConfigurationFilePut("etc/system.properties", "org.switchyard.component.soap.client.port",
                        "8181/cxf"),
                editConfigurationFilePut("etc/system.properties", "org.switchyard.component.soap.standalone.port",
                        "8181"),
                editConfigurationFilePut(
                        "etc/org.ops4j.pax.url.mvn.cfg",
                        "org.ops4j.pax.url.mvn.repositories",
                        "http://repo1.maven.org/maven2@id=central, http://svn.apache.org/repos/asf/servicemix/m2-repo@id=servicemix, http://repository.springsource.com/maven/bundles/release@id=springsource.release, http://repository.springsource.com/maven/bundles/external@id=springsource.external, https://repository.jboss.org/nexus/content/repositories/snapshots@snapshots@noreleases@id=jboss-snapshot, https://repository.jboss.org/nexus/content/repositories/fs-releases@id=fusesource.release"),
                editConfigurationFilePut("etc/org.ops4j.pax.logging.cfg", "log4j.logger.org.switchyard", "DEBUG"),
                editConfigurationFilePut("etc/org.ops4j.pax.logging.cfg", "log4j.logger.org.ops4j.pax.exam", "DEBUG"),
                editConfigurationFilePut("etc/org.ops4j.pax.logging.cfg", "log4j.appender.out.file",
                        "${karaf.home}/../test.log"),
                features("mvn:org.switchyard.karaf/switchyard/2.0.0-SNAPSHOT/xml/features", featureName),
                systemProperty(DeploymentProbe.BUNDLE_NAME_KEY).value(bundleName));
    }
    
    private static Option[] mergeOptions(Option[] defaultOptions, Option[] additionalOptions) {
        if (additionalOptions != null) {
            return options(composite(defaultOptions), composite(additionalOptions));
        }
        return defaultOptions;
    }

    protected String getSoapClientPort() {
         return System.getProperty("org.switchyard.component.soap.client.port", "8181/cxf");
    }
}
