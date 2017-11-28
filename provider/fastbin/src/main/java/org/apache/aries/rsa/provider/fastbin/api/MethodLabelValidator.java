package org.apache.aries.rsa.provider.fastbin.api;

import java.lang.reflect.Method;

public interface MethodLabelValidator extends Intent {
  void validateMethodLabel(Method method, String label);
}
