package intellispaces.framework.core.guide.n2;

import intellispaces.framework.core.system.UnitWrapper;

import java.lang.reflect.Method;

/**
 * Unit method mapper with two qualifier.
 *
 * @param <S> source object handle type.
 * @param <T> target object handle type.
 * @param <Q1> first qualifier object handle type.
 * @param <Q2> second qualifier object handle type.
 */
public class UnitMapper2<S, T, Q1, Q2>
    extends UnitGuide2<S, T, Q1, Q2>
    implements AbstractMapper2<S, T, Q1, Q2>
{
  public UnitMapper2(String tid, UnitWrapper unitInstance, Method guideMethod, int guideActionIndex) {
    super(tid, unitInstance, guideMethod, guideActionIndex);
  }
}
