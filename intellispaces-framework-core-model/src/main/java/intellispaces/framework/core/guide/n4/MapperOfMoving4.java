package intellispaces.framework.core.guide.n4;

import intellispaces.framework.core.exception.TraverseException;
import intellispaces.framework.core.guide.MapperOfMoving;
import intellispaces.framework.core.guide.n5.MapperOfMoving5;

/**
 * Mapper related to moving guide with four qualifiers.
 *
 * @param <S> source handle type.
 * @param <T> target handle type.
 * @param <Q1> first qualifier handle type.
 * @param <Q2> second qualifier handle type.
 * @param <Q3> third qualifier handle type.
 * @param <Q4> fourth qualifier handle type.
 */
public interface MapperOfMoving4<S, T, Q1, Q2, Q3, Q4> extends
    Guide4<S, T, Q1, Q2, Q3, Q4>,
    MapperOfMoving<S, T>,
    MapperOfMoving5<S, T, Q1, Q2, Q3, Q4, Void>
{
  default T map(S source, Q1 qualifier1, Q2 qualifier2, Q3 qualifier3, Q4 qualifier4) throws TraverseException {
    return traverse(source, qualifier1, qualifier2, qualifier3, qualifier4);
  }

  @Override
  default T map(S source, Q1 qualifier1, Q2 qualifier2, Q3 qualifier3, Q4 qualifier4, Void qualifier5) throws TraverseException {
    return traverse(source, qualifier1, qualifier2, qualifier3, qualifier4);
  }

  @Override
  @SuppressWarnings("unchecked")
  default T traverse(S source, Object... qualifiers) throws TraverseException {
    return traverse(source, (Q1) qualifiers[0], (Q2) qualifiers[1], (Q3) qualifiers[2], (Q4) qualifiers[3]);
  }

  @Override
  default T traverse(S source, Q1 qualifier1, Q2 qualifier2, Q3 qualifier3, Q4 qualifier4, Void qualifier5) throws TraverseException {
    return traverse(source, qualifier1, qualifier2, qualifier3, qualifier4);
  }
}
