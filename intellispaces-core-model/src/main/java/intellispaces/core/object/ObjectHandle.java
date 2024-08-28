package intellispaces.core.object;

import intellispaces.core.exception.TraverseException;
import intellispaces.core.space.transition.Transition1;
import intellispaces.javastatements.type.Type;

/**
 * Handle of object.<p/>
 *
 * The handle implements interaction with the object.<p/>
 *
 * The interaction of the system with the object is performed through the object handle.<p/>
 *
 * @param <D> object domain type.
 */
public interface ObjectHandle<D> {

  Type<D> domain();

  Class<D> domainClass();

  boolean isMovable();

  MovableObjectHandle<D> asMovableOrElseThrow();

  @SuppressWarnings("rawtypes")
  default <T, Q> T mapThru(Class<? extends Transition1> transitionClass, Q qualifier) throws TraverseException {
    throw new RuntimeException("Not implemented");
  }
}
