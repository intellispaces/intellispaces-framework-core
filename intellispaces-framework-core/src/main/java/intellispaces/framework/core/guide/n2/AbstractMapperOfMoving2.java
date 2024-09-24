package intellispaces.framework.core.guide.n2;

import intellispaces.common.base.function.QuadFunction;
import intellispaces.common.base.function.TriFunction;
import intellispaces.framework.core.guide.GuideKind;
import intellispaces.framework.core.guide.GuideKinds;

public interface AbstractMapperOfMoving2<S, T, Q1, Q2> extends MapperOfMoving2<S, T, Q1, Q2> {

  @Override
  default GuideKind kind() {
    return GuideKinds.MapperOfMoving2;
  }

  @Override
  default TriFunction<S, Q1, Q2, T> asTriFunction() {
    return this::traverse;
  }

  @Override
  default QuadFunction<S, Q1, Q2, Void, T> asQuadFunction() {
    return this::traverse;
  }
}
