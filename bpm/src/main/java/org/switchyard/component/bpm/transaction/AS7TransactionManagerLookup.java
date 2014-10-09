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
package org.switchyard.component.bpm.transaction;

import org.switchyard.component.common.knowledge.transaction.KnowledgeTransactionManagerLookup;

/**
 * AS7TransactionManagerLookup is <b>DEPRECATED</b>.
 * <br/><br/>
 * See: <a href="http://kverlaen.blogspot.com/2011/07/jbpm5-on-as7-lightning.html">jBPM5 on AS7: Lightning !</a>
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 * @deprecated Use {@link AS7JtaPlatform} instead
 */
@Deprecated
public class AS7TransactionManagerLookup extends KnowledgeTransactionManagerLookup {}
