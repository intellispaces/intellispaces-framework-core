package intellispaces.framework.core.annotation.processor.domain;

import intellispaces.common.annotationprocessor.context.AnnotationProcessingContext;
import intellispaces.common.base.type.Type;
import intellispaces.common.javastatement.customtype.CustomType;
import intellispaces.common.javastatement.method.MethodStatement;
import intellispaces.common.javastatement.reference.CustomTypeReference;
import intellispaces.common.javastatement.type.Types;
import intellispaces.framework.core.annotation.ObjectHandle;
import intellispaces.framework.core.common.NameConventionFunctions;
import intellispaces.framework.core.object.ObjectHandleTypes;
import intellispaces.framework.core.space.channel.ChannelFunctions;
import intellispaces.framework.core.space.domain.DomainFunctions;
import intellispaces.framework.core.traverse.TraverseType;

import javax.annotation.processing.RoundEnvironment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CommonObjectHandleGenerator extends AbstractDomainObjectHandleGenerator {
  private String objectHandleBunch;
  private boolean isAlias;
  private String primaryObjectHandle;
  private String primaryDomainSimpleName;
  private String primaryDomainTypeArguments;
  private String domainType;

  public CommonObjectHandleGenerator(CustomType initiatorType, CustomType domainType) {
    super(initiatorType, domainType);
  }

  @Override
  public boolean isRelevant(AnnotationProcessingContext context) {
    return true;
  }

  @Override
  protected ObjectHandleTypes getObjectHandleType() {
    return ObjectHandleTypes.Common;
  }

  @Override
  public String artifactName() {
    return NameConventionFunctions.getObjectHandleTypename(annotatedType.className(), ObjectHandleTypes.Common);
  }

  @Override
  protected String templateName() {
    return "/common_object_handle.template";
  }

  protected Map<String, Object> templateVariables() {
    Map<String, Object> vars = new HashMap<>();
    vars.put("generatedAnnotation", makeGeneratedAnnotation());
    vars.put("importedClasses", context.getImports());
    vars.put("packageName", context.packageName());
    vars.put("sourceClassName", sourceClassCanonicalName());
    vars.put("sourceClassSimpleName", sourceClassSimpleName());
    vars.put("classSimpleName", context.generatedClassSimpleName());
    vars.put("movableClassSimpleName", movableClassSimpleName());
    vars.put("domainTypeParamsFull", domainTypeParamsFull);
    vars.put("domainTypeParamsBrief", domainTypeParamsBrief);
    vars.put("objectHandleBunch", objectHandleBunch);
    vars.put("domainType", domainType);
    vars.put("domainMethods", methods);
    vars.put("isAlias", isAlias);
    vars.put("primaryObjectHandle", primaryObjectHandle);
    vars.put("primaryDomainTypeArguments", primaryDomainTypeArguments);
    vars.put("primaryDomainSimpleName", primaryDomainSimpleName);
    return vars;
  }

  @Override
  protected boolean analyzeAnnotatedType(RoundEnvironment roundEnv) {
    context.generatedClassCanonicalName(artifactName());
    context.addImport(sourceClassCanonicalName());
    context.addImport(Type.class);
    context.addImport(Types.class);
    context.addImport(ObjectHandle.class);

    domainTypeParamsFull = annotatedType.typeParametersFullDeclaration();
    domainTypeParamsBrief = annotatedType.typeParametersBriefDeclaration();
    objectHandleBunch = context.addToImportAndGetSimpleName(
        NameConventionFunctions.getBunchObjectHandleTypename(annotatedType.className())
    );
    analyzeObjectHandleMethods(annotatedType, roundEnv);

    List<CustomTypeReference> equivalentDomains = DomainFunctions.getEquivalentDomains(annotatedType);
    isAlias = !equivalentDomains.isEmpty();
    if (isAlias) {
      CustomTypeReference nearEquivalentDomain = equivalentDomains.get(0);
      CustomTypeReference mainEquivalentDomain = equivalentDomains.get(equivalentDomains.size() - 1);

      primaryObjectHandle = getObjectHandleDeclaration(nearEquivalentDomain, ObjectHandleTypes.Common);
      primaryDomainTypeArguments = nearEquivalentDomain.typeArgumentsDeclaration(context::addToImportAndGetSimpleName);
      primaryDomainSimpleName = context.addToImportAndGetSimpleName(mainEquivalentDomain.targetType().canonicalName());
      domainType = buildDomainType(mainEquivalentDomain.targetType(), mainEquivalentDomain.typeArguments());
    } else {
      domainType = buildDomainType(annotatedType, (List) annotatedType.typeParameters());
    }
    return true;
  }

  @Override
  protected Stream<MethodStatement> getObjectHandleMethods(
      CustomType customType, RoundEnvironment roundEnv
  ) {
    return super.getObjectHandleMethods(customType, roundEnv)
        .filter(this::isNotDomainClassGetter)
        .filter(m -> !m.returnType().orElseThrow().isNamedReference())
        .filter(m -> ChannelFunctions.getTraverseTypes(m).stream().noneMatch(TraverseType::isMovingBased));
  }
}
