package tech.intellispacesframework.core.object;

import tech.intellispacesframework.core.exception.TraverseException;
import tech.intellispacesframework.core.transition.TransitionMethod1;

/**
 * Handle to movable object.
 *
 * @param <D> object domain type.
 */
public interface MovableObjectHandle<D> extends ObjectHandle<D> {

  <Q> MovableObjectHandle<D> moveThru(String cid, Q qualifier) throws TraverseException;

  <Q> MovableObjectHandle<D> moveThru(TransitionMethod1<? super D, ? extends D, Q> channelMethod, Q qualifier) throws TraverseException;
}
