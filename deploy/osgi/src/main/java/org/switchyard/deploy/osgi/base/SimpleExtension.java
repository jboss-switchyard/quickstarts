/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.deploy.osgi.base;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SimpleExtension implements Extension {

    protected final Bundle bundle;
    protected final BundleContext bundleContext;
    protected final AtomicBoolean destroyed = new AtomicBoolean(false);

    public SimpleExtension(Bundle bundle) {
        this.bundle = bundle;
        this.bundleContext = bundle.getBundleContext();
    }

    public boolean isDestroyed() {
        synchronized (getLock()) {
            return destroyed.get();
        }
    }

    public Bundle getBundle() {
        return bundle;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void start() throws Exception {
        synchronized (getLock()) {
            if (destroyed.get()) {
                return;
            }
            if (bundle.getState() != Bundle.ACTIVE) {
                return;
            }
            if (bundle.getBundleContext() != bundleContext) {
                return;
            }
            doStart();
        }
    }

    public void destroy() throws Exception {
        synchronized (getLock()) {
            destroyed.set(true);
        }
        doDestroy();
    }

    protected Object getLock() {
        return this;
    }

    protected abstract void doStart() throws Exception;

    protected abstract void doDestroy() throws Exception;

}
