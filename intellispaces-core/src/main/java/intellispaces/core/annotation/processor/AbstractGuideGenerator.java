package intellispaces.core.annotation.processor;

import intellispaces.actions.common.string.StringActions;
import intellispaces.actions.runner.Runner;
import intellispaces.commons.exception.UnexpectedViolationException;
import intellispaces.commons.type.TypeFunctions;
import intellispaces.core.annotation.Guide;
import intellispaces.core.common.NameConventionFunctions;
import intellispaces.core.guide.GuideFunctions;
import intellispaces.core.guide.GuideKind;
import intellispaces.core.guide.n0.Mapper0;
import intellispaces.core.guide.n0.Mover0;
import intellispaces.core.guide.n1.Mapper1;
import intellispaces.core.guide.n1.Mover1;
import intellispaces.core.guide.n2.Mapper2;
import intellispaces.core.guide.n2.Mover2;
import intellispaces.core.guide.n3.Mapper3;
import intellispaces.core.guide.n3.Mover3;
import intellispaces.core.guide.n4.Mapper4;
import intellispaces.core.guide.n4.Mover4;
import intellispaces.core.guide.n5.Mapper5;
import intellispaces.core.guide.n5.Mover5;
import intellispaces.core.object.ObjectFunctions;
import intellispaces.core.space.transition.TransitionFunctions;
import intellispaces.core.traverse.TraverseType;
import intellispaces.core.traverse.TraverseTypes;
import intellispaces.javastatements.customtype.CustomType;
import intellispaces.javastatements.method.MethodParam;
import intellispaces.javastatements.method.MethodStatement;
import intellispaces.javastatements.reference.NotPrimitiveReference;
import intellispaces.javastatements.reference.TypeReference;

import javax.annotation.processing.RoundEnvironment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractGuideGenerator extends AbstractGenerator {
  protected final TraverseType traverseType;
  protected final MethodStatement transitionMethod;
  private String guideClassSimpleName;
  private String guideMethod;
  private String baseMethod;
  private String guideTypeParamsFull;

  public AbstractGuideGenerator(
      TraverseType traverseType, CustomType annotatedType, MethodStatement transitionMethod
  ) {
    super(annotatedType);
    this.traverseType = traverseType;
    this.transitionMethod = transitionMethod;
  }

  protected abstract CustomType getDomainType();

  abstract protected String getGuideTypeParamDeclaration();

  protected abstract List<MethodParam> getQualifierMethodParams();

  protected abstract String getGuideClassCanonicalName();

  @Override
  public String getArtifactName() {
    return getGuideClassCanonicalName();
  }

  @Override
  protected String templateName() {
    return "/guide.template";
  }

  @Override
  protected Map<String, Object> templateVariables() {
    Map<String, Object> vars = new HashMap<>();
    vars.put("generatedAnnotation", makeGeneratedAnnotation());
    vars.put("packageName", context.packageName());
    vars.put("sourceClassName", sourceClassCanonicalName());
    vars.put("sourceClassSimpleName", sourceClassSimpleName());
    vars.put("classSimpleName", context.generatedClassSimpleName());
    vars.put("transitionClassSimpleName", transitionClassSimpleName());
    vars.put("guideTypeParamsFull", guideTypeParamsFull);
    vars.put("guideClassSimpleName", guideClassSimpleName);
    vars.put("guideTypeParams", buildGuideTypeParams(Function.identity()));
    vars.put("guideMethod", guideMethod);
    vars.put("baseMethod", baseMethod);
    vars.put("importedClasses", context.getImports());
    return vars;
  }

  @Override
  protected boolean analyzeAnnotatedType(RoundEnvironment roundEnv) {
    context.generatedClassCanonicalName(getGuideClassCanonicalName());
    if (annotatedType.isNested()) {
      context.addImport(annotatedType.canonicalName());
    }
    context.addImport(Guide.class);
    context.addImport(TransitionFunctions.class);
    analyzeGuideType();
    return true;
  }

  protected String buildGuideMethod() {
    return buildGuideMethod(Function.identity());
  }

  protected String buildGuideMethod(Function<TypeReference, TypeReference> typeReplacer) {
    var sb = new StringBuilder();
    if (AnnotationProcessorFunctions.isVoidType(transitionMethod.returnType().orElseThrow())) {
      sb.append("void");
    } else {
      sb.append(buildTargetObjectHandleDeclaration(typeReplacer));
    }
    sb.append(" ");
    sb.append(transitionMethod.name());
    sb.append("(");
    sb.append(buildSourceObjectHandleDeclaration(typeReplacer));
    sb.append(" source");
    for (MethodParam param : getQualifierMethodParams()) {
      sb.append(", ");
      sb.append(buildObjectHandleDeclaration(param.type(), typeReplacer));
      sb.append(" ");
      sb.append(param.name());
    }
    sb.append(");");
    return sb.toString();
  }

  private void analyzeGuideType() {
    Class<?> guideClass = getGuideClass();
    guideClassSimpleName = getGuideClassSimpleName(guideClass);
    guideTypeParamsFull = getGuideTypeParamDeclaration();
    baseMethod = buildBaseMethod(guideClass, Function.identity());
    guideMethod = buildGuideMethod();
  }

  private String getGuideClassSimpleName(Class<?> guideClass) {
    String abstractGuideCanonicalName = TypeFunctions.addPrefixToSimpleName(
        "Abstract", guideClass.getCanonicalName()
    );
    return context.addToImportAndGetSimpleName(abstractGuideCanonicalName);
  }

  private Class<?> getGuideClass() {
    int qualifierCount = getQualifierMethodParams().size();
    if (TraverseTypes.Mapping == traverseType) {
      return switch (qualifierCount) {
        case 0 -> Mapper0.class;
        case 1 -> Mapper1.class;
        case 2 -> Mapper2.class;
        case 3 -> Mapper3.class;
        case 4 -> Mapper4.class;
        case 5 -> Mapper5.class;
        default -> throw UnexpectedViolationException.withMessage("Unsupported number of mapper guide qualifies: {}",
            qualifierCount);
      };
    } else {
      return switch (qualifierCount) {
        case 0 -> Mover0.class;
        case 1 -> Mover1.class;
        case 2 -> Mover2.class;
        case 3 -> Mover3.class;
        case 4 -> Mover4.class;
        case 5 -> Mover5.class;
        default -> throw UnexpectedViolationException.withMessage("Unsupported number of mapper guide qualifies: {}",
            qualifierCount);
      };
    }
  }

  protected String buildGuideTypeParams(Function<TypeReference, TypeReference> typeReplacer) {
    var sb = new StringBuilder();
    sb.append("<");
    sb.append(buildSourceObjectHandleDeclaration(typeReplacer));
    sb.append(", ");
    sb.append(buildTargetObjectHandleDeclaration(typeReplacer));
    for (MethodParam param : getQualifierMethodParams()) {
      sb.append(", ");
      sb.append(buildObjectHandleDeclaration(param.type(), typeReplacer));
    }
    sb.append(">");
    return sb.toString();
  }

  private String buildBaseMethod(Class<?> guideClass, Function<TypeReference, TypeReference> typeReplacer) {
    GuideKind guideKind = GuideFunctions.getGuideKind(guideClass);

    var sb = new StringBuilder();
    sb.append("default ");
    sb.append(buildTargetObjectHandleDeclaration(typeReplacer));
    sb.append(guideKind.isMapper() ? " map(" : " move(");
    sb.append(buildSourceObjectHandleDeclaration(typeReplacer));
    sb.append(" ").append("source");
    for (MethodParam param : getQualifierMethodParams()) {
      sb.append(", ");
      sb.append(buildObjectHandleDeclaration(param.type(), typeReplacer));
      sb.append(" ");
      sb.append(param.name());
    }
    sb.append(") {\n  ");
    if (!AnnotationProcessorFunctions.isVoidType(transitionMethod.returnType().orElseThrow())) {
      sb.append("return ");
    }
    sb.append(transitionMethod.name());
    sb.append("(").append("source");
    for (MethodParam param : getQualifierMethodParams()) {
      sb.append(", ");
      sb.append(param.name());
    }
    sb.append(");\n");
    if (AnnotationProcessorFunctions.isVoidType(transitionMethod.returnType().orElseThrow())) {
      sb.append("  return null;\n");
    }
    sb.append("}");
    return sb.toString();
  }

  protected String buildSourceObjectHandleDeclaration(
      Function<TypeReference, TypeReference> typeReplacer
  ) {
    return buildObjectHandleDeclaration(getDomainType());
  }

  protected String buildTargetObjectHandleDeclaration(Function<TypeReference, TypeReference> typeReplacer) {
    TypeReference returnType = transitionMethod.returnType().orElseThrow();
    return buildObjectHandleDeclaration(returnType, typeReplacer);
  }

  protected String buildObjectHandleDeclaration(
      TypeReference type, Function<TypeReference, TypeReference> typeReplacer
  ) {
    type = typeReplacer.apply(type);
    if (type.isNamedReference()) {
      return type.asNamedReferenceOrElseThrow().name();
    } else {
      String canonicalName = ObjectFunctions.getBaseObjectHandleTypename(type, typeReplacer);
      String name = type.isCustomTypeReference() ? context.addToImportAndGetSimpleName(canonicalName) : canonicalName;
      if (type.isCustomTypeReference() && !type.asCustomTypeReferenceOrElseThrow().typeArguments().isEmpty()) {
        var sb = new StringBuilder();
        sb.append(name);
        sb.append("<");
        Runner commaAppender = StringActions.skippingFirstTimeCommaAppender(sb);
        for (NotPrimitiveReference typeArg : type.asCustomTypeReferenceOrElseThrow().typeArguments()) {
          commaAppender.run();
          sb.append(buildObjectHandleDeclaration(typeArg, typeReplacer));
        }
        sb.append(">");
        return sb.toString();
      }
      return name;
    }
  }

  private String buildObjectHandleDeclaration(TypeReference type, boolean nested) {
    if (type.isWildcard() && nested) {
      return "?";
    } else if (type.isNamedReference()) {
      return type.asNamedReferenceOrElseThrow().name();
    } else {
      String canonicalName = ObjectFunctions.getBaseObjectHandleTypename(type);
      String simpleName = context.addToImportAndGetSimpleName(canonicalName);
      if (type.isCustomTypeReference() && !type.asCustomTypeReferenceOrElseThrow().typeArguments().isEmpty()) {
        var sb = new StringBuilder();
        sb.append(simpleName);
        sb.append("<");
        Runner commaAppender = StringActions.skippingFirstTimeCommaAppender(sb);
        for (NotPrimitiveReference typeArg : type.asCustomTypeReferenceOrElseThrow().typeArguments()) {
          commaAppender.run();
          sb.append(typeArg.actualDeclaration());
        }
        sb.append(">");
        return sb.toString();
      }
      return simpleName;
    }
  }

  private String buildObjectHandleDeclaration(CustomType type) {
    String canonicalName = ObjectFunctions.getBaseObjectHandleTypename(type);
    String simpleName = context.addToImportAndGetSimpleName(canonicalName);
    if (!type.typeParameters().isEmpty()) {
      var sb = new StringBuilder();
      sb.append(simpleName);
      sb.append("<");
      Runner commaAppender = StringActions.skippingFirstTimeCommaAppender(sb);
      for (NotPrimitiveReference typeParam : type.typeParameters()) {
        commaAppender.run();
        sb.append(buildObjectHandleDeclaration(typeParam, true));
      }
      sb.append(">");
      return sb.toString();
    }
    return simpleName;
  }

  private String transitionClassSimpleName() {
    String canonicalName = NameConventionFunctions.getTransitionClassCanonicalName(
        annotatedType.packageName(), getDomainType(), transitionMethod
    );
    return context.addToImportAndGetSimpleName(canonicalName);
  }
}
