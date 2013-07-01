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
 
package org.switchyard.component.resteasy.resource;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.netty.HttpServerPipelineFactory;
import org.jboss.resteasy.plugins.server.netty.HttpsServerPipelineFactory;
import org.jboss.resteasy.plugins.server.netty.RequestDispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;

/**
 * An HTTP server that sends back the content of the received HTTP request
 * in a pretty plaintext form.
 *
 * Keep this class untill https://issues.jboss.org/browse/RESTEASY-794 moves to AS7
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * @author Norman Maurer
 * @version $Rev: 2080 $, $Date: 2010-01-26 18:04:19 +0900 (Tue, 26 Jan 2010) $
 */
public class NettyJaxrsServer implements EmbeddedJaxrsServer
{
   /**
    * Default port in which the standalone publisher is started.
    */
   public static final int DEFAULT_PORT = 8080;

   /**
    * System property to adjust the port in which the standalone publisher is started.
    */
   public static final String DEFAULT_PORT_PROPERTY = "org.switchyard.component.resteasy.standalone.port";

   protected ServerBootstrap bootstrap;
   protected Channel channel;
   protected int port = DEFAULT_PORT;
   protected ResteasyDeployment deployment = new ResteasyDeployment();
   protected String root = "";
   protected SecurityDomain domain;
   private int ioWorkerCount = Runtime.getRuntime().availableProcessors() * 2;
   private int executorThreadCount = 16;
   private SSLContext sslContext;
   private int maxRequestSize = 1024 * 1024 * 10;

   public void setSSLContext(SSLContext sslContext) 
   {
      this.sslContext = sslContext;
   }
   
   /**
    * Specify the worker count to use. For more informations about this please see the javadocs of {@link NioServerSocketChannelFactory}
    * 
    * @param ioWorkerCount
    */
   public void setIoWorkerCount(int ioWorkerCount) 
   {
       this.ioWorkerCount = ioWorkerCount;
   }
   
   /**
    * Set the number of threads to use for the Executor. For more informations please see the javadocs of {@link OrderedMemoryAwareThreadPoolExecutor}. 
    * If you want to disable the use of the {@link ExecutionHandler} specify a value <= 0.  This should only be done if you are 100% sure that you don't have any blocking
    * code in there.
    * 
    * 
    * @param executorThreadCount
    */
   public void setExecutorThreadCount(int executorThreadCount)
   {
       this.executorThreadCount = executorThreadCount;
   }

   /**
    * Set the max. request size in bytes. If this size is exceed we will send a "413 Request Entity Too Large" to the client.
    * 
    * @param maxRequestSize the max request size. This is 10mb by default.
    */
   public void setMaxRequestSize(int maxRequestSize) 
   {
       this.maxRequestSize  = maxRequestSize;
   }
   
   public int getPort()
   {
      return port;
   }

   public void setPort(int port)
   {
      this.port = port;
   }

   @Override
   public void setDeployment(ResteasyDeployment deployment)
   {
      this.deployment = deployment;
   }

   @Override
   public void setRootResourcePath(String rootResourcePath)
   {
      root = rootResourcePath;
      if (root != null && root.equals("/")) root = "";
   }

   @Override
   public ResteasyDeployment getDeployment()
   {
      return deployment;
   }

   @Override
   public void setSecurityDomain(SecurityDomain sc)
   {
      this.domain = sc;
   }

   @Override
   public void start()
   {
      deployment.start();
      RequestDispatcher dispatcher = new RequestDispatcher((SynchronousDispatcher)deployment.getDispatcher(), deployment.getProviderFactory(), domain);

      // Configure the server.
      bootstrap = new ServerBootstrap(
              new NioServerSocketChannelFactory(
                      Executors.newCachedThreadPool(),
                      Executors.newCachedThreadPool(), 
                      ioWorkerCount));

      ChannelPipelineFactory factory;
      if (sslContext == null) {
          factory = new HttpServerPipelineFactory(dispatcher, root, executorThreadCount, maxRequestSize);
      } else {
          factory = new HttpsServerPipelineFactory(dispatcher, root, executorThreadCount, maxRequestSize, sslContext);
      }
      // Set up the event pipeline factory.
      bootstrap.setPipelineFactory(factory);

      // Bind and start to accept incoming connections.
      channel = bootstrap.bind(new InetSocketAddress(getDefaultPort()));
   }

   @Override
   public void stop()
   {
      channel.close().awaitUninterruptibly();
      bootstrap.releaseExternalResources();
      deployment.stop();
   }

    /**
     * Returns the port where the standalone publisher will be started
     * @return the port
     */
    static int getDefaultPort() {
        int port = DEFAULT_PORT;
        final String portAsStr = System.getProperty(DEFAULT_PORT_PROPERTY);
        if (portAsStr != null) {
            port = Integer.parseInt(portAsStr);
        }
        return port;
    }
}
