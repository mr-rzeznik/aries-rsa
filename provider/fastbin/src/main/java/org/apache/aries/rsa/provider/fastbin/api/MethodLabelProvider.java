/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.aries.rsa.provider.fastbin.api;

import java.lang.reflect.Method;

public interface MethodLabelProvider extends Intent {

  /**
   * Add additional label to method signature.
   *
   * @param method which will be remotely invoked
   * @return an additional label for method signature
   */
  String provideLabel(Method method);

  /**
   * Indicates if method signature should be rebuild. Checked before every invocation.
   * If false, cached signature will be used.
   * If provider label changes over time, then this method should return
   * false when appropriate.
   * @param method which will be remotely invoked
   * @return if true, method signature will be created, if false cache will be used
   */
  boolean needsUpdate(Method method);
}
