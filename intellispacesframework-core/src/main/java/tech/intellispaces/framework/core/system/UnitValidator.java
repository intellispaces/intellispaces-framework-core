package tech.intellispaces.framework.core.system;

import tech.intellispaces.framework.core.annotation.Guide;
import tech.intellispaces.framework.core.annotation.Module;
import tech.intellispaces.framework.core.annotation.Projection;
import tech.intellispaces.framework.core.annotation.Shutdown;
import tech.intellispaces.framework.core.annotation.Startup;
import tech.intellispaces.framework.core.annotation.Unit;
import tech.intellispaces.framework.core.exception.ConfigurationException;
import tech.intellispaces.framework.core.object.ObjectFunctions;
import tech.intellispaces.framework.core.space.domain.DomainFunctions;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

/**
 * Unit declaration validator.
 */
class UnitValidator {

  public void validateUnitDeclaration(Class<?> unitClass, boolean mainUnit) {
    validateModuleUnitAnnotations(unitClass, mainUnit);
    validateStartupMethods(unitClass, mainUnit);
    validateShutdownMethods(unitClass, mainUnit);
    validateProjectionMethods(unitClass);
  }

  private void validateModuleUnitAnnotations(Class<?> unitClass, boolean mainUnit) {
    if (mainUnit) {
      if (!unitClass.isAnnotationPresent(tech.intellispaces.framework.core.annotation.Module.class)) {
        throw ConfigurationException.withMessage("Class {} is not marked with annotation {}",
            unitClass.getCanonicalName(), Module.class.getSimpleName());
      }
    } else {
      if (
          !unitClass.isAnnotationPresent(tech.intellispaces.framework.core.annotation.Unit.class) &&
          !unitClass.isAnnotationPresent(tech.intellispaces.framework.core.annotation.Guide.class)
      ) {
        throw ConfigurationException.withMessage("Class {} is not marked with annotation {} or {}",
            unitClass.getCanonicalName(), Unit.class.getSimpleName(), Guide.class.getSimpleName());
      }
    }
  }

  private void validateStartupMethods(Class<?> unitClass, boolean mainUnit) {
    List<Method> methods = Arrays.stream(unitClass.getDeclaredMethods())
        .filter(m -> m.isAnnotationPresent(Startup.class))
        .toList();
    if (mainUnit) {
      if (methods.size() > 1) {
        throw ConfigurationException.withMessage("Main unit {} contains more that one startup methods", unitClass.getCanonicalName());
      }
    } else {
      if (!methods.isEmpty()) {
        throw ConfigurationException.withMessage("Additional unit cannot contain startup method, but unit {} contains it", unitClass.getCanonicalName());
      }
    }
    if (!methods.isEmpty()) {
      checkStartupOrShutdownMethod(methods.get(0));
    }
  }

  private void validateShutdownMethods(Class<?> unitClass, boolean mainUnit) {
    List<Method> methods = Arrays.stream(unitClass.getDeclaredMethods())
        .filter(m -> m.isAnnotationPresent(Shutdown.class))
        .toList();
    if (mainUnit) {
      if (methods.size() > 1) {
        throw ConfigurationException.withMessage("Main unit {} contains more that one shutdown methods", unitClass.getCanonicalName());
      }
    } else {
      if (!methods.isEmpty()) {
        throw ConfigurationException.withMessage("Additional unit cannot contain shutdown method, but unit {} contains it", unitClass.getCanonicalName());
      }
    }
    if (!methods.isEmpty()) {
      checkStartupOrShutdownMethod(methods.get(0));
    }
  }

  private void checkStartupOrShutdownMethod(Method method) {
    checkMethodParams(method);
  }

  private void validateProjectionMethods(Class<?> unitClass) {
    Arrays.stream(unitClass.getDeclaredMethods())
        .filter(m -> m.isAnnotationPresent(Projection.class))
        .forEach(this::checkProjectionMethod);
  }

  private void checkProjectionMethod(Method method) {
    Class<?> returnType = method.getReturnType();
    if (returnType == void.class || returnType == Void.class) {
      throw ConfigurationException.withMessage("Method of the projection '{}' in unit {} should return value",
          method.getName(), method.getDeclaringClass().getCanonicalName());
    }
    if (!isValidType(returnType)) {
      throw ConfigurationException.withMessage("Method of the projection '{}' in unit {} should return object handle or domain class",
          method.getName(), method.getDeclaringClass().getCanonicalName());
    }
    checkMethodParams(method);
  }

  private void checkMethodParams(Method method) {
    for (Parameter param : method.getParameters()) {
      Class<?> paramType = param.getType();
      if (!isValidType(paramType)) {
        throw ConfigurationException.withMessage("Parameter '{}' of method '{}' in unit {} should be object handle or domain class",
            param.getName(), method.getName(), method.getDeclaringClass().getCanonicalName());
      }
    }
  }

  private boolean isValidType(Class<?> type) {
    return ObjectFunctions.isObjectHandleClass(type) || DomainFunctions.isDomainClass(type);
  }
}
