package intellispaces.framework.core.guide.n0;

import intellispaces.framework.core.system.ObjectHandleWrapper;

import java.lang.reflect.Method;

/**
 * Attached to object handle mapper related to moving.<p/>
 *
 * Attached guide can be used exclusively with this object handle only.
 *
 * @param <S> mapper source object type.
 */
public class ObjectMapperOfMoving0<S extends ObjectHandleWrapper, T>
    extends ObjectGuide0<S, T>
    implements AbstractMapperOfMoving0<S, T>
{
  public ObjectMapperOfMoving0(
      String tid,
      Class<S> objectHandleClass,
      Method guideMethod,
      int transitionIndex
  ) {
    super(tid, objectHandleClass, guideMethod, transitionIndex);
  }
}
