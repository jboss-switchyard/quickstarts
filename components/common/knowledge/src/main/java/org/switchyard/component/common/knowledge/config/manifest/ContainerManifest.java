/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.config.manifest;

import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieContainer;
import org.switchyard.component.common.knowledge.CommonKnowledgeMessages;

/**
 * ContainerManifest.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public final class ContainerManifest extends Manifest {

    private final String _baseName;
    private final ReleaseId _releaseId;
    private final boolean _scan;
    private final Long _scanInterval;
    private final String _sessionName;

    private KieContainer _kieContainer;

    /**
     * Creates a new, empty ContainerManifest.
     */
    public ContainerManifest() {
        this(null, (ReleaseId)null, false, null, null);
    }

    /**
     * Creates a new ContainerManifest.
     * @param baseName the base name
     * @param releaseId the release id
     * @param scan if scanning is enabled
     * @param scanInterval the scan interval
     * @param sessionName the session name
     */
    public ContainerManifest(String baseName, String releaseId, boolean scan, Long scanInterval, String sessionName) {
        this(baseName, toReleaseId(releaseId), scan, scanInterval, sessionName);
    }

    /**
     * Creates a new ContainerManifest.
     * @param baseName the base name
     * @param releaseId the release id
     * @param scan if scanning is enabled
     * @param scanInterval the scan interval
     * @param sessionName the session name
     */
    public ContainerManifest(String baseName, ReleaseId releaseId, boolean scan, Long scanInterval, String sessionName) {
        _baseName = baseName;
        _releaseId = releaseId;
        _scan = scan;
        if (scanInterval == null) {
            scanInterval = Long.valueOf(60000);
        }
        long si = scanInterval.longValue();
        if (si < 1) {
            throw CommonKnowledgeMessages.MESSAGES.containerScanIntervalMustBePositive();
        }
        _scanInterval = scanInterval;
        _sessionName = sessionName;
    }

    /**
     * Gets the base name.
     * @return the base name
     */
    public String getBaseName() {
        return _baseName;
    }

    /**
     * Gets the release id.
     * @return the release id
     */
    public ReleaseId getReleaseId() {
        return _releaseId;
    }

    /**
     * If scanning is enabled.
     * @return if scanning is enabled
     */
    public boolean isScan() {
        return _scan;
    }

    /**
     * Gets the scan interval.
     * @return the scan interval
     */
    public Long getScanInterval() {
        return _scanInterval;
    }

    /**
     * Gets the session name.
     * @return the session name
     */
    public String getSessionName() {
        return _sessionName;
    }

    /**
     * Gets the kie container.
     * @return the kie container
     */
    public KieContainer getKieContainer() {
        return _kieContainer;
    }

    /**
     * Sets the kie container.
     * @param kieContainer the kie container
     */
    public void setKieContainer(KieContainer kieContainer) {
        _kieContainer = kieContainer;
    }

    /**
     * Removes a ContainerManifest from the Environment.
     * @param environment the Environment
     * @return the ContainerManifest
     */
    public static ContainerManifest removeFromEnvironment(Environment environment) {
        return Manifest.removeFromEnvironment(environment, ContainerManifest.class);
    }

    /**
     * Converts a colon (':') separated string into a ReleaseId.
     * @param releaseId the string
     * @return the ReleaseId
     */
    public static ReleaseId toReleaseId(String releaseId) {
        if (releaseId != null) {
            String[] gav = releaseId.split(":", 3);
            String groupId = gav.length > 0 ? gav[0] : null;
            String artifactId = gav.length > 1 ? gav[1] : null;
            String version = gav.length > 2 ? gav[2] : null;
            return KieServices.Factory.get().newReleaseId(groupId, artifactId, version);
        }
        return null;
    }

}
