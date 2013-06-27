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

package org.switchyard.quickstarts.soap.attachment;

import java.awt.Image;

import javax.xml.bind.annotation.XmlType;

@XmlType(name="imageRequest", namespace = "urn:switchyard-quickstart:soap-binding-rpc:1.0")
public class ImageRequest {

   private Image data;


   public Image getData() {
      return data;
   }

   public void setData(Image data) {
      this.data = data;
   }
}
