package intellispaces.framework.core.action;

import intellispaces.common.action.AbstractAction2;
import intellispaces.common.javastatement.type.Type;
import intellispaces.framework.core.guide.n1.Mover1;
import intellispaces.framework.core.space.transition.Transition1;
import intellispaces.framework.core.system.Modules;

class MoveThruTransition1Action<R, S, Q> extends AbstractAction2<R, S, Q> {
  private final Type<S> sourceType;
  private final Class<? extends Transition1> transitionClass;
  private final Mover1<S, R, Q> autoMover;

  MoveThruTransition1Action(
      Type<S> sourceType,
      Class<? extends Transition1> transitionClass
  ) {
    this.sourceType = sourceType;
    this.transitionClass = transitionClass;
    this.autoMover = Modules.current().autoMoverThruTransition1(sourceType, transitionClass);
  }

  @Override
  public R execute(S source, Q qualifier) {
    return autoMover.move(source, qualifier);
  }
}
