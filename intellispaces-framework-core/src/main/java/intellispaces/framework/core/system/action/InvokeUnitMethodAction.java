package intellispaces.framework.core.system.action;

import intellispaces.common.action.AbstractAction0;
import intellispaces.common.base.exception.UnexpectedViolationException;
import intellispaces.framework.core.exception.ConfigurationException;
import intellispaces.framework.core.system.kernel.KernelFunctions;
import intellispaces.framework.core.system.UnitWrapper;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

public class InvokeUnitMethodAction<R> extends AbstractAction0<R> {
  private final UnitWrapper unitInstance;
  private final Method unitMethod;

  public InvokeUnitMethodAction(UnitWrapper unitInstance, Method unitMethod) {
    this.unitInstance = unitInstance;
    this.unitMethod = unitMethod;
  }

  public Method getUnitMethod() {
    return unitMethod;
  }

  @Override
  @SuppressWarnings("unchecked")
  public R execute() {
    Object[] arguments = makeMethodArguments();
    try {
      return (R) unitMethod.invoke(unitInstance, arguments);
    } catch (Exception e) {
      throw UnexpectedViolationException.withCauseAndMessage(e, "Could not to invoke unit method ''{0}''",
          unitMethod.getName());
    }
  }

  private Object[] makeMethodArguments() {
    var arguments = new ArrayList<>();
    for (Parameter param : unitMethod.getParameters()) {
      Object projection = KernelFunctions.currentModule()
          .projectionRegistry()
          .getProjection(param.getName(), param.getType());
      if (projection == null) {
        throw ConfigurationException.withMessage("Cannot to resolve parameter ''{0}'' in method ''{1}'' in unit {2}",
            param.getName(), unitMethod.getName(), unitMethod.getDeclaringClass().getCanonicalName());
      }
      arguments.add(projection);
    }
    return arguments.toArray();
  }
}
