package intellispaces.framework.core.guide.n1;

import intellispaces.framework.core.system.ObjectHandleWrapper;

import java.lang.reflect.Method;

/**
 * Attached to object handle mover guide.<p/>
 *
 * Attached guide can be used exclusively with this object handle only.
 *
 * @param <S> source object handle type.
 * @param <Q> qualified object handle type.
 */
public class ObjectMover1<S extends ObjectHandleWrapper, Q>
    extends ObjectGuide1<S, S, Q>
    implements AbstractMover1<S, Q>
{
  public ObjectMover1(
      String cid,
      Class<S> objectHandleClass,
      Method guideMethod,
      int channelIndex
  ) {
    super(cid, objectHandleClass, guideMethod, channelIndex);
  }
}
