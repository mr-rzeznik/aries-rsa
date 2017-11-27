package org.apache.aries.rsa.provider.fastbin.api;

import java.lang.reflect.Method;

/**
 *
 * @author Devs-PS
 */
public interface MethodSignatureIntent {

  String INTENT_TYPE = "fastbin.intent";
  String INTENT_NAME = "fastbin.intent.name";

  /**
   * Add additional label in method signature.
   *
   * @param method add an additional label to it's signature
   * @return an additional label for method signature
   */
  String onMethodSignature(Method method);

  void validateMethodSignature(Method method, String label);
}
