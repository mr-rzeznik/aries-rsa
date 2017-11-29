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
