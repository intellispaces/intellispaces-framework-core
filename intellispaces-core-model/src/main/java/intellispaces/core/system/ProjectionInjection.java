package intellispaces.core.system;

public interface ProjectionInjection extends ProjectionReference, Injection {

  Class<?> unitClass();

  boolean isDefined();
}
