package intellispaces.core.guide;

import intellispaces.commons.exception.UnexpectedViolationException;
import intellispaces.commons.type.TypeFunctions;
import intellispaces.core.annotation.Mapper;
import intellispaces.core.annotation.Mover;
import intellispaces.core.annotation.Ordinal;
import intellispaces.core.annotation.Transition;
import intellispaces.core.common.NameConventionFunctions;
import intellispaces.core.guide.n0.Mapper0;
import intellispaces.core.guide.n0.Mover0;
import intellispaces.core.guide.n0.ObjectMapper0;
import intellispaces.core.guide.n0.ObjectMover0;
import intellispaces.core.guide.n0.UnitMapper0;
import intellispaces.core.guide.n1.Mapper1;
import intellispaces.core.guide.n1.Mover1;
import intellispaces.core.guide.n1.ObjectMapper1;
import intellispaces.core.guide.n1.ObjectMover1;
import intellispaces.core.guide.n1.UnitMapper1;
import intellispaces.core.guide.n2.Mapper2;
import intellispaces.core.guide.n2.Mover2;
import intellispaces.core.guide.n2.ObjectMapper2;
import intellispaces.core.guide.n2.ObjectMover2;
import intellispaces.core.guide.n3.Mapper3;
import intellispaces.core.guide.n3.Mover3;
import intellispaces.core.guide.n4.Mapper4;
import intellispaces.core.guide.n4.Mover4;
import intellispaces.core.guide.n5.Mapper5;
import intellispaces.core.guide.n5.Mover5;
import intellispaces.core.space.transition.TransitionFunctions;
import intellispaces.core.system.UnitWrapper;
import intellispaces.core.traverse.TraverseTypes;
import intellispaces.javastatements.customtype.CustomTypes;
import intellispaces.javastatements.method.MethodFunctions;
import intellispaces.javastatements.method.MethodStatement;
import intellispaces.javastatements.method.Methods;
import intellispaces.javastatements.reference.TypeReference;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class GuideFunctions {

  private GuideFunctions() {}

  public static boolean isGuideType(TypeReference type) {
    return type.isCustomTypeReference() &&
        type.asCustomTypeReferenceOrElseThrow().targetType().hasAnnotation(intellispaces.core.annotation.Guide.class);
  }

  public static boolean isGuideMethod(MethodStatement method) {
    return method.hasAnnotation(intellispaces.core.annotation.Mapper.class) ||
        method.hasAnnotation(intellispaces.core.annotation.Mover.class);
  }

  public static boolean isGuideMethod(Method method) {
    return method.isAnnotationPresent(intellispaces.core.annotation.Mapper.class) ||
        method.isAnnotationPresent(intellispaces.core.annotation.Mover.class);
  }

  public static Transition getObjectGuideTransitionAnnotation(Method objectHandleMethod) {
    return TransitionFunctions.getAttachedGuideTransitionAnnotation(objectHandleMethod);
  }

  public static String getUnitGuideTid(Object unitInstance, Method guideMethod) {
    return TransitionFunctions.getUnitGuideTid(unitInstance, guideMethod);
  }

  public static List<Guide<?, ?>> loadObjectGuides(Class<?> objectHandleClass) {
    List<Guide<?, ?>> guides = new ArrayList<>();
    for (Method method : objectHandleClass.getDeclaredMethods()) {
      if (isGuideMethod(method)) {
        Transition transition = getObjectGuideTransitionAnnotation(method);
        if (TraverseTypes.Mapping == TransitionFunctions.getTraverseType(transition)) {
          guides.add(createObjectMapper(objectHandleClass, transition.value(), method));
        } else {
          guides.add(createObjectMover(objectHandleClass, transition.value(), method));
        }
      }
    }
    return guides;
  }

  public static List<Guide<?, ?>> loadUnitGuides(Class<?> unitClass, UnitWrapper unitInstance) {
    List<Guide<?, ?>> guides = new ArrayList<>();
    for (Method method : unitClass.getDeclaredMethods()) {
      if (method.isAnnotationPresent(Mapper.class)) {
          guides.add(createMapper(unitInstance, method));
      } else if (method.isAnnotationPresent(Mover.class)) {
          guides.add(createMover(unitInstance, method));
      }
    }
    return guides;
  }

  public static GuideKind getGuideKind(Class<?> guideClass) {
    GuideKind guideKind = GUIDE_CLASS_TO_KIND.get(guideClass);
    if (guideKind == null) {
      throw UnexpectedViolationException.withMessage("Unsupported guide class: " + guideClass.getCanonicalName());
    }
    return guideKind;
  }

  private static Guide<?, ?> createMapper(UnitWrapper unitInstance, Method guideMethod) {
    String tid = getUnitGuideTid(unitInstance, guideMethod);
    int guideIndex = getUnitGuideIndex(unitInstance, guideMethod);
    int qualifiersCount = guideMethod.getParameterCount();
    return switch (qualifiersCount) {
      case 1 -> new UnitMapper0<>(tid, unitInstance, guideMethod, guideIndex);
      case 2 -> new UnitMapper1<>(tid, unitInstance, guideMethod, guideIndex);
      default -> throw UnexpectedViolationException.withMessage("Unsupported number of guide qualifiers: {}",
          qualifiersCount);
    };
  }

  private static Guide<?, ?> createMover(Object unitInstance, Method guideMethod) {
    String tid = getUnitGuideTid(unitInstance, guideMethod);
    throw new UnsupportedOperationException("Not implemented");
  }

  @SuppressWarnings("unchecked, rawtypes")
  private static <S, T> Guide<S, T> createObjectMapper(
      Class<S> objectHandleClass, String tid, Method guideMethod
  ) {
    int transitionIndex = getTransitionIndex(objectHandleClass, guideMethod);
    int qualifiersCount = guideMethod.getParameterCount();
    return switch (qualifiersCount) {
      case 0 -> new ObjectMapper0<>(tid, (Class) objectHandleClass, guideMethod, transitionIndex);
      case 1 -> new ObjectMapper1<>(tid, (Class) objectHandleClass, guideMethod, transitionIndex);
      case 2 -> new ObjectMapper2<>(tid, (Class) objectHandleClass, guideMethod, transitionIndex);
      default -> throw UnexpectedViolationException.withMessage("Unsupported number of guide qualifiers: {}",
          qualifiersCount);
    };
  }

  @SuppressWarnings("unchecked, rawtypes")
  private static <S, T> Guide<S, T> createObjectMover(
      Class<S> objectHandleClass, String tid, Method guideMethod
  ) {
    int transitionIndex = getTransitionIndex(objectHandleClass, guideMethod);
    int qualifiersCount = guideMethod.getParameterCount();
    return switch (qualifiersCount) {
      case 0 -> new ObjectMover0<>(tid, (Class) objectHandleClass, guideMethod, transitionIndex);
      case 1 -> new ObjectMover1<>(tid, (Class) objectHandleClass, guideMethod, transitionIndex);
      case 2 -> new ObjectMover2<>(tid, (Class) objectHandleClass, guideMethod, transitionIndex);
      default -> throw UnexpectedViolationException.withMessage("Unsupported number of guide qualifiers: {}",
          qualifiersCount);
    };
  }

  public static int getTransitionIndex(Class<?> objectHandleClass, Method guideMethod) {
    String implClassCanonicalName = NameConventionFunctions.getObjectHandleImplementationCanonicalName(
        objectHandleClass
    );
    Optional<Class<?>> objectHandleImplClass = TypeFunctions.getClass(implClassCanonicalName);
    if (objectHandleImplClass.isEmpty()) {
      throw UnexpectedViolationException.withMessage("Could not get object handle implementation class {}",
          implClassCanonicalName);
    }

    Optional<MethodStatement> objectHandleImplGuideMethod = MethodFunctions.getOverrideMethod(
        CustomTypes.of(objectHandleImplClass.get()),
        Methods.of(guideMethod)
    );
    if (objectHandleImplGuideMethod.isEmpty()) {
      throw UnexpectedViolationException.withMessage("Could not find override method in object handle implementation " +
          "class {}. Method {}", objectHandleImplClass.get(), guideMethod.getName());
    }
    Optional<Ordinal> indexAnnotation = objectHandleImplGuideMethod.get().selectAnnotation(Ordinal.class);
    if (indexAnnotation.isEmpty()) {
      throw UnexpectedViolationException.withMessage("Method {} does not contain annotation {}",
          guideMethod.getName(), Ordinal.class.getCanonicalName());
    }
    return indexAnnotation.get().value();
  }

  private static int getUnitGuideIndex(UnitWrapper unitInstance, Method guideMethod) {
    Class<?> unitWrapperClass = unitInstance.getClass();
    Optional<MethodStatement> overrideGuideMethod = MethodFunctions.getOverrideMethod(
        CustomTypes.of(unitWrapperClass),
        Methods.of(guideMethod)
    );
    if (overrideGuideMethod.isEmpty()) {
      throw UnexpectedViolationException.withMessage("Could not find override method in unit wrapper " +
          "class {}. Method {}", unitWrapperClass, guideMethod.getName());
    }
    Optional<Ordinal> indexAnnotation = overrideGuideMethod.get().selectAnnotation(Ordinal.class);
    if (indexAnnotation.isEmpty()) {
      throw UnexpectedViolationException.withMessage("Method {} does not contain annotation {}",
          guideMethod.getName(), Ordinal.class.getCanonicalName());
    }
    return indexAnnotation.get().value();
  }

  private static final Map<Class<?>, GuideKind> GUIDE_CLASS_TO_KIND = new HashMap<>();
  static {
    GUIDE_CLASS_TO_KIND.put(Mapper0.class, GuideKinds.Mapper0);
    GUIDE_CLASS_TO_KIND.put(Mapper1.class, GuideKinds.Mapper1);
    GUIDE_CLASS_TO_KIND.put(Mapper2.class, GuideKinds.Mapper2);
    GUIDE_CLASS_TO_KIND.put(Mapper3.class, GuideKinds.Mapper3);
    GUIDE_CLASS_TO_KIND.put(Mapper4.class, GuideKinds.Mapper4);
    GUIDE_CLASS_TO_KIND.put(Mapper5.class, GuideKinds.Mapper5);
    GUIDE_CLASS_TO_KIND.put(Mover0.class, GuideKinds.Mover0);
    GUIDE_CLASS_TO_KIND.put(Mover1.class, GuideKinds.Mover1);
    GUIDE_CLASS_TO_KIND.put(Mover2.class, GuideKinds.Mover2);
    GUIDE_CLASS_TO_KIND.put(Mover3.class, GuideKinds.Mover3);
    GUIDE_CLASS_TO_KIND.put(Mover4.class, GuideKinds.Mover4);
    GUIDE_CLASS_TO_KIND.put(Mover5.class, GuideKinds.Mover5);
  }
}
