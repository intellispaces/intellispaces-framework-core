package intellispaces.framework.core.guide.n3;

import intellispaces.framework.core.exception.TraverseException;
import intellispaces.framework.core.traverse.plan.DeclarativePlan;
import intellispaces.framework.core.traverse.plan.TraverseExecutor;

/**
 * Three times parametrized automatic mapper of moving.
 *
 * <p>Automatic guide builds the traverse plan itself.
 *
 * @param <S> source object handle type.
 * @param <T> target object handle type.
 * @param <Q1> first qualifier handle type.
 * @param <Q2> second qualifier handle type.
 * @param <Q3> third qualifier handle type.
 */
public class AutoMapperOfMoving3<S, T, Q1, Q2,  Q3> implements AbstractMapperOfMoving3<S, T, Q1, Q2, Q3> {
  private final String cid;
  private final TraverseExecutor traverseExecutor;
  private final DeclarativePlan declarativeTaskPlan;

  public AutoMapperOfMoving3(String cid, DeclarativePlan declarativeTaskPlan, TraverseExecutor traverseExecutor) {
    this.cid = cid;
    this.declarativeTaskPlan = declarativeTaskPlan;
    this.traverseExecutor = traverseExecutor;
  }

  @Override
  public String cid() {
    return cid;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T traverse(S source, Q1 qualifier1, Q2 qualifier2, Q3 qualifier3) throws TraverseException {
    return (T) declarativeTaskPlan.execute(source, qualifier1, qualifier2, qualifier3, traverseExecutor);
  }
}
