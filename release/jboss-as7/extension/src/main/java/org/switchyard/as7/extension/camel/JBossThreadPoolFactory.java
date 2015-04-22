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
package org.switchyard.as7.extension.camel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.camel.spi.ThreadPoolFactory;
import org.apache.camel.spi.ThreadPoolProfile;
import org.apache.camel.util.concurrent.RejectableScheduledThreadPoolExecutor;
import org.apache.camel.util.concurrent.RejectableThreadPoolExecutor;
import org.apache.camel.util.concurrent.SizedScheduledExecutorService;
import org.jboss.as.naming.context.NamespaceContextSelector;

/**
 * ThreadPoolFactory specific to JBoss which allows the namespace context to be
 * set for any threads created by Camel and allows for the resolution of the CDI
 * BeanManager (in addition to anything else useful in java:comp).
 * 
 * This class is identical to Camel's DefaultThreadPoolFactory with the exception of 
 * the overloaded ThreadPoolExecutors.  There is no clean way to replace those using 
 * DefaultThreadPoolFactory directly, so we need to replicate that code and modify
 * the behavior inline.
 */
public class JBossThreadPoolFactory implements ThreadPoolFactory {
    
    private NamespaceContextSelector _contextSelector;
    
    public JBossThreadPoolFactory(NamespaceContextSelector contextSelector) {
        _contextSelector = contextSelector;
    }

    @Override
    public ScheduledExecutorService newScheduledThreadPool(ThreadPoolProfile profile, ThreadFactory threadFactory) {
        RejectedExecutionHandler rejectedExecutionHandler = profile.getRejectedExecutionHandler();
        if (rejectedExecutionHandler == null) {
            rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
        }

        ScheduledThreadPoolExecutor answer = new RejectableScheduledThreadPoolExecutor(
                profile.getPoolSize(), threadFactory, rejectedExecutionHandler) {
            
            protected void beforeExecute(Thread t, Runnable r) {
                super.beforeExecute(t, r);
                before();
            }
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                after();
            }
        };

        // need to wrap the thread pool in a sized to guard against the problem that the
        // JDK created thread pool has an unbounded queue (see class javadoc), which mean
        // we could potentially keep adding tasks, and run out of memory.
        if (profile.getMaxPoolSize() > 0) {
            return new SizedScheduledExecutorService(answer, profile.getMaxQueueSize());
        } else {
            return answer;
        }
    }
    
    public ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, 
                TimeUnit.SECONDS, new SynchronousQueue<Runnable>()) {
            
            protected void beforeExecute(Thread t, Runnable r) {
                super.beforeExecute(t, r);
                before();
            }
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                after();
            }
        };
    }
    
    
    @Override
    public ExecutorService newThreadPool(ThreadPoolProfile profile, ThreadFactory factory) {
        return newThreadPool(profile.getPoolSize(), 
                             profile.getMaxPoolSize(), 
                             profile.getKeepAliveTime(),
                             profile.getTimeUnit(),
                             profile.getMaxQueueSize(), 
                             profile.getRejectedExecutionHandler(),
                             factory);
    }

    public ExecutorService newThreadPool(int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit timeUnit,
                                         int maxQueueSize, RejectedExecutionHandler rejectedExecutionHandler,
                                         ThreadFactory threadFactory) throws IllegalArgumentException {

        // the core pool size must be higher than 0
        if (corePoolSize < 1) {
            throw new IllegalArgumentException("CorePoolSize must be >= 1, was " + corePoolSize);
        }

        // validate max >= core
        if (maxPoolSize < corePoolSize) {
            throw new IllegalArgumentException("MaxPoolSize must be >= corePoolSize, was " + maxPoolSize + " >= " + corePoolSize);
        }

        BlockingQueue<Runnable> workQueue;
        if (corePoolSize == 0 && maxQueueSize <= 0) {
            // use a synchronous queue for direct-handover (no tasks stored on the queue)
            workQueue = new SynchronousQueue<Runnable>();
            // and force 1 as pool size to be able to create the thread pool by the JDK
            corePoolSize = 1;
            maxPoolSize = 1;
        } else if (maxQueueSize <= 0) {
            // use a synchronous queue for direct-handover (no tasks stored on the queue)
            workQueue = new SynchronousQueue<Runnable>();
        } else {
            // bounded task queue to store tasks on the queue
            workQueue = new LinkedBlockingQueue<Runnable>(maxQueueSize);
        }

        ThreadPoolExecutor answer = new RejectableThreadPoolExecutor(
                corePoolSize, maxPoolSize, keepAliveTime, timeUnit, workQueue) {
            
            protected void beforeExecute(Thread t, Runnable r) {
                super.beforeExecute(t, r);
                before();
            }
            
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                after();
            }
        };
        answer.setThreadFactory(threadFactory);
        if (rejectedExecutionHandler == null) {
            rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
        }
        answer.setRejectedExecutionHandler(rejectedExecutionHandler);
        return answer;
    }
    

    /**
     * Called from beforeExecute in all ThreadExecutors created by this class.
     */
    private void before() {
        // establish naming context for the current thread
        NamespaceContextSelector.pushCurrentSelector(_contextSelector);
    }
    
    /**
     * Called from afterExecute in all ThreadExecutors created by this class.
     */
    private void after() {
        // clean up naming context
        NamespaceContextSelector.popCurrentSelector();
    }
}


