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
package org.switchyard.deploy.osgi.base;

import org.osgi.framework.Bundle;

import java.util.List;

/**
 * CompoundExtension.
 */
public class CompoundExtension extends SimpleExtension {

    final List<Extension> _extensions;

    /**
     * Create a new CompoundExtension.
     * @param bundle bundle
     * @param extensions extensions
     */
    public CompoundExtension(Bundle bundle, List<Extension> extensions) {
        super(bundle);
        _extensions = extensions;
    }

    @Override
    protected void doStart() throws Exception {
        for (Extension extension : _extensions) {
            extension.start();
        }
    }

    @Override
    protected void doDestroy() throws Exception {
        for (Extension extension : _extensions) {
            extension.destroy();
        }
    }
}
