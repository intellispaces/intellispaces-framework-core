package intellispaces.framework.core.action;

import intellispaces.common.action.AbstractAction2;
import intellispaces.common.base.type.Type;
import intellispaces.framework.core.guide.GuideForm;
import intellispaces.framework.core.guide.n1.Mover1;
import intellispaces.framework.core.space.channel.Channel1;
import intellispaces.framework.core.system.Modules;

class MoveThruChannel1Action<S, Q> extends AbstractAction2<S, S, Q> {
  private final Type<S> sourceType;
  private final Class<? extends Channel1> channelClass;
  private final GuideForm guideForm;
  private final Mover1<S, Q> autoMover;

  MoveThruChannel1Action(
      Type<S> sourceType,
      Class<? extends Channel1> channelClass,
      GuideForm guideForm
  ) {
    this.sourceType = sourceType;
    this.channelClass = channelClass;
    this.guideForm = guideForm;
    this.autoMover = Modules.current().autoMoverThruChannel1(sourceType, channelClass, guideForm);
  }

  @Override
  public S execute(S source, Q qualifier) {
    return autoMover.move(source, qualifier);
  }
}
