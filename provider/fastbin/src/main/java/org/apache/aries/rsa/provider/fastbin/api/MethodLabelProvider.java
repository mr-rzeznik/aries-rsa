package org.apache.aries.rsa.provider.fastbin.api;

import java.lang.reflect.Method;

public interface MethodLabelProvider extends Intent {

  /**
   * Add additional label in method signature.
   *
   * @param method add an additional label to it's signature
   * @return an additional label for method signature
   */
  String provideLabel(Method method);

  boolean needsRebuild(Method method);
}
