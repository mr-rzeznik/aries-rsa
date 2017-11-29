package org.apache.aries.rsa.provider.fastbin.api;

import java.lang.reflect.Method;

public interface MethodLabelValidator extends Intent {

  /**
   * Validates provided method signature's label.
   * If label is invalid, intent should throw an excpetion.
   * @param method remotely invoked method
   * @param label provided label for this intent
   */
  void validateMethodLabel(Method method, String label);
}
