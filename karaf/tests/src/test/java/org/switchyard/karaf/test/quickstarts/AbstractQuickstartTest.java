package org.switchyard.karaf.test.quickstarts;

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

import java.io.File;

import org.junit.After;
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
    protected ExamSystem system;
    protected TestContainer testContainer;

    @After
    public void after() throws Exception {
        if (testContainer != null) {
            final TestContainer tc = testContainer;
            testContainer = null;
            tc.stop();
        }
    }
    
    @Test
    public void testBundleActivation() throws Exception {
        executeProbe(DeploymentProbe.class, "testBundleActivation");
    }
    
    protected void startTestContainer(String featureName, String bundleName) throws Exception {
        system = PaxExamRuntime.createTestSystem(config(featureName, bundleName));
        testContainer = PaxExamRuntime.createContainer(system);
        testContainer.start();
        Thread.sleep(5000);
    }

    protected void executeProbe(Class clazz, String operation) throws Exception {
        TestProbeBuilder probeBuilder = system.createProbe();
        probeBuilder.addTest(clazz, operation, new Object[]{});
        TestProbeProvider probeProvider = probeBuilder.build();
        testContainer.installProbe(probeProvider.getStream());
        for (TestAddress test : probeProvider.getTests()) {
            testContainer.call(test);
        }
    }

    private Option[] config(String featureName, String bundleName) {
        return new Option[] {
            karafDistributionConfiguration().frameworkUrl(maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("tar.gz").versionAsInProject())
            .karafVersion(MavenUtils.getArtifactVersion("org.apache.karaf", "apache-karaf")).name("Apache Karaf").unpackDirectory(new File("target/karaf/"+featureName)).useDeployFolder(false),
            keepRuntimeFolder(),
            logLevel(LogLevel.INFO),
            configureConsole().ignoreLocalConsole(),
            editConfigurationFileExtend("etc/config.properties", "org.osgi.framework.system.packages.extra", "sun.misc"),
            editConfigurationFileExtend("etc/org.ops4j.pax.url.mvn.cfg", "org.ops4j.pax.url.mvn.repositories", "https://repository.jboss.org/nexus/content/repositories/snapshots@snapshots@noreleases@id=jboss-snapshot"),
            editConfigurationFileExtend("etc/org.ops4j.pax.url.mvn.cfg", "org.ops4j.pax.url.mvn.repositories", "https://repository.jboss.org/nexus/content/repositories/fs-releases@id=fusesource.release"),
            editConfigurationFilePut("etc/org.ops4j.pax.logging.cfg", "log4j.logger.org.switchyard", "DEBUG"),
            editConfigurationFilePut("etc/org.ops4j.pax.logging.cfg", "log4j.logger.org.ops4j.pax.exam", "DEBUG"),
            features("mvn:org.switchyard.karaf/switchyard/2.0.0-SNAPSHOT/xml/features", featureName),
            systemProperty(DeploymentProbe.BUNDLE_NAME_KEY).value(bundleName)
        };
    }
}
