/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.camel.netty.binding;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDPClient {

    public static void main(String[] args) throws Exception {
        MulticastSocket clientSocket = new MulticastSocket();
        InetAddress group = InetAddress.getByName("localhost");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Message body to send over UDP: ");
        byte[] datagramBody = reader.readLine().getBytes();
        DatagramPacket packet = new DatagramPacket(datagramBody, 0, datagramBody.length, group, 3940);
        clientSocket.send(packet);

        Thread.sleep(100);
        clientSocket.close();
    }

}
