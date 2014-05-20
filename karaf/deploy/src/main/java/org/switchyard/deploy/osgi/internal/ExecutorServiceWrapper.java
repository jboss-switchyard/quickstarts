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
package org.switchyard.deploy.osgi.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ExecutorServiceWrapper.
 */
public class ExecutorServiceWrapper extends AbstractExecutorService implements Runnable {

    private final ExecutorService _delegate;
    private final ConcurrentLinkedQueue<Runnable> _queue = new ConcurrentLinkedQueue<Runnable>();
    private final AtomicBoolean _triggered = new AtomicBoolean();
    private final AtomicBoolean _shutdown = new AtomicBoolean();
    private Thread _runningThread;

    /**
     * Create a new instance of ExecutorServiceWrapper.
     * @param delegate ExecutorService delegate
     */
    public ExecutorServiceWrapper(ExecutorService delegate) {
        this._delegate = delegate;
    }

    @Override
    public void shutdown() {
        _shutdown.set(true);
    }

    @Override
    public List<Runnable> shutdownNow() {
        List<Runnable> pending = new ArrayList<Runnable>();
        if (_shutdown.compareAndSet(false, true)) {
            Runnable runnable;
            while ((runnable = _queue.poll()) != null) {
                pending.add(runnable);
            }
        }
        return pending;
    }

    @Override
    public boolean isShutdown() {
        return _shutdown.get();
    }

    @Override
    public boolean isTerminated() {
        return _delegate.isTerminated() || isShutdown() && _queue.isEmpty() && !_triggered.get();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long millis = unit.toMillis(timeout);
        if (millis > 0) {
            long max = System.currentTimeMillis() + millis;
            synchronized (_triggered) {
                while (System.currentTimeMillis() < max) {
                    if (isTerminated()) {
                        return true;
                    } else {
                        _triggered.wait(millis);
                    }
                }
            }
        }
        return isTerminated();
    }

    @Override
    public void execute(Runnable command) {
        if (isShutdown()) {
            throw new RejectedExecutionException("Executor has been shut down");
        }
        _queue.add(command);
        triggerExecution();
    }

    protected void triggerExecution() {
        if (_triggered.compareAndSet(false, true)) {
            _delegate.execute(this);
        }
    }

    @Override
    public void run() {
        try {
            Runnable runnable;
            synchronized (_triggered) {
                _runningThread = Thread.currentThread();
            }
            while (true) {
                runnable = _queue.poll();
                if (runnable == null) {
                    return;
                }
                try {
                    runnable.run();
                } catch (Throwable e) {
                    Thread thread = Thread.currentThread();
                    thread.getUncaughtExceptionHandler().uncaughtException(thread, e);
                }
            }
        } finally {
            synchronized (_triggered) {
                _runningThread = null;
                _triggered.set(false);
                _triggered.notifyAll();
            }
            if (!isShutdown() && !_queue.isEmpty()) {
                triggerExecution();
            }
        }
    }
}
