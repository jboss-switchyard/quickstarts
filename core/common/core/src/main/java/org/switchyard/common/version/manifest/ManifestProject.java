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
package org.switchyard.common.version.manifest;

import static org.switchyard.common.version.manifest.ManifestVersion.getMainAttributesValue;

import java.util.jar.Manifest;

import org.switchyard.common.version.BaseProject;

/**
 * ManifestProject.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
final class ManifestProject extends BaseProject {

    ManifestProject(Manifest manifest) {
        super(getMainAttributesValue(manifest, "Project-Group-Id"),
              getMainAttributesValue(manifest, "Project-Artifact-Id"),
              getMainAttributesValue(manifest, "Project-Packaging"),
              getMainAttributesValue(manifest, "Project-Name"),
              getMainAttributesValue(manifest, "Project-Description"),
              getMainAttributesValue(manifest, "Project-URL"),
              getMainAttributesValue(manifest, "Project-Version"));
    }

}
