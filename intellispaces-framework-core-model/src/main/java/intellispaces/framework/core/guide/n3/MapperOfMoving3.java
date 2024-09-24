package intellispaces.framework.core.guide.n3;

import intellispaces.common.base.function.QuadFunction;
import intellispaces.framework.core.exception.TraverseException;
import intellispaces.framework.core.guide.MapperOfMoving;
import intellispaces.framework.core.guide.n4.MapperOfMoving4;
import intellispaces.framework.core.guide.n5.MapperOfMoving5;

/**
 * Mapper related to moving guide with three qualifiers.
 *
 * @param <S> source object type.
 * @param <T> target object type.
 * @param <Q1> first qualifier type.
 * @param <Q2> second qualifier type.
 * @param <Q3> third qualifier type.
 */
public interface MapperOfMoving3<S, T, Q1, Q2, Q3> extends
    Guide3<S, T, Q1, Q2, Q3>,
    MapperOfMoving<S, T>,
    MapperOfMoving4<S, T, Q1, Q2, Q3, Void>,
    MapperOfMoving5<S, T, Q1, Q2, Q3, Void, Void>
{
  QuadFunction<S, Q1, Q2, Q3, T> asQuadFunction();

  default T map(S source, Q1 qualifier1, Q2 qualifier2, Q3 qualifier3) throws TraverseException {
    return traverse(source, qualifier1, qualifier2, qualifier3);
  }

  @Override
  default T map(S source, Q1 qualifier1, Q2 qualifier2, Q3 qualifier3, Void qualifier4) throws TraverseException {
    return traverse(source, qualifier1, qualifier2, qualifier3);
  }

  @Override
  default T map(S source, Q1 qualifier1, Q2 qualifier2, Q3 qualifier3, Void qualifier4, Void qualifier5) throws TraverseException {
    return traverse(source, qualifier1, qualifier2, qualifier3);
  }

  @Override
  @SuppressWarnings("unchecked")
  default T traverse(S source, Object... qualifiers) throws TraverseException {
    return traverse(source, (Q1) qualifiers[0], (Q2) qualifiers[1], (Q3) qualifiers[2]);
  }

  @Override
  default T traverse(S source, Q1 qualifier1, Q2 qualifier2, Q3 qualifier3, Void qualifier4) throws TraverseException {
    return traverse(source, qualifier1, qualifier2, qualifier3);
  }

  @Override
  default T traverse(S source, Q1 qualifier1, Q2 qualifier2, Q3 qualifier3, Void qualifier4, Void qualifier5) throws TraverseException {
    return traverse(source, qualifier1, qualifier2, qualifier3);
  }
}
