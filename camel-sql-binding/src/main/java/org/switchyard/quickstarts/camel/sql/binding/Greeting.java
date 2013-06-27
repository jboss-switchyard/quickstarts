/*
 * 2012 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.quickstarts.camel.sql.binding;

/**
 * Simple greeting pojo.
 */
public class Greeting {

    private Integer _id;
    private String _receiver;
    private String _sender;

    /**
     * Creates greeting.
     * 
     * @param id Greeting id.
     * @param receiver Person to receive greet.
     * @param sender Greeting sender.
     */
    public Greeting(Integer id, String receiver, String sender) {
        this._id = id;
        this._receiver = receiver;
        this._sender = sender;
    }

    /**
     * Gets greet id.
     * @return Greeting id.
     */
    public Integer getId() {
        return _id;
    }

    /**
     * Creates greeting.
     * 
     * @param receiver Person to receive greet.
     * @param sender Greeting sender.
     */
    public Greeting(String receiver, String sender) {
        this._receiver = receiver;
        this._sender = sender;
    }

    /**
     * Gets receiver name.
     * @return Receiver name.
     */
    public String getReceiver() {
        return _receiver;
    }

    /**
     * Sets receiver name.
     * @param name Name of the receiver.
     */
    public void setReceiver(String name) {
        this._receiver = name;
    }

    /**
     * Gets sender name.
     * @return Sender name.
     */
    public String getSender() {
        return _sender;
    }

    /**
     * Sets sender name.
     * @param sender Sender name
     */
    public void setSender(String sender) {
        this._sender = sender;
    }

    @Override
    public String toString() {
        return "Greeting [id=" + _id + ", sender=" + _sender + ", receiver = " + _receiver + "]";
    }

}
