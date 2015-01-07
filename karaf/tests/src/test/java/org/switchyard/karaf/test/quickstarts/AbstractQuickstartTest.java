package org.switchyard.karaf.test.quickstarts;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.vmOptions;
import static org.ops4j.pax.exam.CoreOptions.when;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFileExtend;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;
import static org.switchyard.karaf.test.quickstarts.PhaseListener.Phase.ADD_TESTS;
import static org.switchyard.karaf.test.quickstarts.PhaseListener.Phase.BUILD_PROBE;
import static org.switchyard.karaf.test.quickstarts.PhaseListener.Phase.CREATE_CONTAINER;
import static org.switchyard.karaf.test.quickstarts.PhaseListener.Phase.CREATE_PROBE;
import static org.switchyard.karaf.test.quickstarts.PhaseListener.Phase.CREATE_SYSTEM;
import static org.switchyard.karaf.test.quickstarts.PhaseListener.Phase.EXECUTE_PROBE;
import static org.switchyard.karaf.test.quickstarts.PhaseListener.Phase.INSTALL_PROBE;
import static org.switchyard.karaf.test.quickstarts.PhaseListener.Phase.START_CONTAINER;

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
import org.osgi.framework.Constants;
import org.switchyard.karaf.test.quickstarts.PhaseListener.Phase;

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
        startTestContainer(featureName, bundleName, null);
    }

    protected static void startTestContainer(String featureName, String bundleName, Option[] additionalOptions) throws Exception {
        startTestContainer(featureName, bundleName, additionalOptions, null);
    }

    protected static void startTestContainer(String featureName, String bundleName, Option[] additionalOptions, Class<? extends DeploymentProbe> probeClass) throws Exception {
        startTestContainer(featureName, bundleName, additionalOptions, probeClass, null);
    }

    protected static void startTestContainer(String featureName, String bundleName, Option[] additionalOptions, Class<? extends DeploymentProbe> probeClass, PhaseListener phaseListener) throws Exception {
        if (probeClass == null) {
            probeClass = DeploymentProbe.class;
        }
        long time = System.currentTimeMillis();
        system = PaxExamRuntime.createTestSystem(mergeOptions(config(featureName, bundleName), additionalOptions));
        time = post(phaseListener, CREATE_SYSTEM, time);
        testContainer = PaxExamRuntime.createContainer(system);
        time = post(phaseListener, CREATE_CONTAINER, time);
        testContainer.start();
        time = post(phaseListener, START_CONTAINER, time);
        try {
            // install the probe
            TestProbeBuilder probeBuilder = system.createProbe();
            time = post(phaseListener, CREATE_PROBE, time);
            probeBuilder.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional");
            probeBuilder.addTests(probeClass, probeClass.getMethods());
            time = post(phaseListener, ADD_TESTS, time);
            testProbe = probeBuilder.build();
            time = post(phaseListener, BUILD_PROBE, time);
            testContainer.installProbe(testProbe.getStream());
            time = post(phaseListener, INSTALL_PROBE, time);
            // wait for SwitchYard application activation
            executeProbe("testBundleActivation");
            time = post(phaseListener, EXECUTE_PROBE, time);
        } catch (Exception e) {
            // cleanup
            after();
            throw e;
        }
    }

    private static long post(PhaseListener phaseListener, Phase phase, long time) throws Exception {
        long duration = System.currentTimeMillis() - time;
        System.out.println(String.format("Phase %s took %sh:%sm:%ss:%sms", phase.name(),
                MILLISECONDS.toHours(duration),
                MILLISECONDS.toMinutes(duration) - HOURS.toMinutes(MILLISECONDS.toHours(duration)),
                MILLISECONDS.toSeconds(duration) - MINUTES.toSeconds(MILLISECONDS.toMinutes(duration)),
                MILLISECONDS.toMillis(duration) - SECONDS.toMillis(MILLISECONDS.toSeconds(duration))));
        if (phaseListener != null) {
            String plp = phaseListener.getClass().getName() + ".post(" + phase.name() + ")";
            System.out.println(plp + " executing...");
            phaseListener.post(phase);
            System.out.println(plp + " done.");
        }
        return System.currentTimeMillis();
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
        final String localMavenRepo = System.getProperty("maven.repo.local", "");
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
                editConfigurationFilePut("etc/org.ops4j.pax.logging.cfg", "log4j.logger.org.switchyard", "DEBUG"),
                editConfigurationFilePut("etc/org.ops4j.pax.logging.cfg", "log4j.logger.org.ops4j.pax.exam", "DEBUG"),
                editConfigurationFilePut("etc/org.ops4j.pax.logging.cfg", "log4j.appender.out.file",
                        "${karaf.home}/../test.log"),
                editConfigurationFilePut("etc/system.properties", "org.switchyard.component.http.standalone.port",
                        "8181"),
                editConfigurationFilePut("etc/system.properties", "org.switchyard.component.soap.client.port",
                        "8181/cxf"),
                editConfigurationFilePut("etc/system.properties", "org.switchyard.component.soap.standalone.port",
                        "8181"),
                editConfigurationFilePut("etc/system.properties", "org.switchyard.component.camel.ftps.storefile",
                        "../../../test-classes/quickstarts/camel-ftp-binding/ftpclient.jks"),
                editConfigurationFilePut("etc/system.properties", "org.switchyard.component.camel.sftp.knownhosts",
                        "../../../test-classes/quickstarts/camel-ftp-binding/known_hosts_sftp"),
                editConfigurationFilePut("etc/system.properties", "org.switchyard.component.camel.sftp.keyfile",
                        "../../../test-classes/quickstarts/camel-ftp-binding/id_sftp_rsa"),
                editConfigurationFilePut(
                        "etc/org.ops4j.pax.url.mvn.cfg",
                        "org.ops4j.pax.url.mvn.repositories",
                        "https://repository.jboss.org/nexus/content/groups/public@id=jboss-public-repository-group,http://repo1.maven.org/maven2@id=central, http://svn.apache.org/repos/asf/servicemix/m2-repo@id=servicemix, http://repository.springsource.com/maven/bundles/release@id=springsource.release, http://repository.springsource.com/maven/bundles/external@id=springsource.external, https://repository.jboss.org/nexus/content/repositories/snapshots@snapshots@noreleases@id=jboss-snapshot, https://repository.jboss.org/nexus/content/repositories/fs-releases@id=fusesource.release"),
                features(maven().groupId("org.switchyard.karaf").artifactId("switchyard").type("xml").classifier("features").versionAsInProject().getURL(), featureName),
                systemProperty(DeploymentProbe.BUNDLE_NAME_KEY).value(bundleName),
                vmOptions("-Xmx1G", "-XX:MaxPermSize=256M"),
                when(localMavenRepo.length() > 0).useOptions(systemProperty("org.ops4j.pax.url.mvn.localRepository").value(localMavenRepo)));
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
