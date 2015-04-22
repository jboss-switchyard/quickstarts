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

package org.switchyard.component.bean.omservice.basic;

import org.switchyard.component.bean.Service;
import org.switchyard.component.bean.omservice.model.OrderRequest;
import org.switchyard.component.bean.omservice.model.OrderResponse;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@Service(BasicOrderManagementService.class)
public class BasicOrderManagementServiceImpl implements BasicOrderManagementService {

    @Override
    public OrderResponse createOrder(OrderRequest request) {

        return new OrderResponse(request.orderId);
    }
}
