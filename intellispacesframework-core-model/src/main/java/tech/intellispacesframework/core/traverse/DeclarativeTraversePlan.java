package tech.intellispacesframework.core.traverse;

/**
 * Declarative traverse plan.
 *
 * <p>Declarative traverse plan says what needs to be done, but does not say how to do it.
 */
public interface DeclarativeTraversePlan extends TraversePlan {

  @Override
  default boolean isDeclarative() {
    return true;
  }
}
