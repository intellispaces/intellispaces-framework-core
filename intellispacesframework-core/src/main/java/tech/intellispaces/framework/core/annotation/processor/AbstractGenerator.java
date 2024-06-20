package tech.intellispaces.framework.core.annotation.processor;

import tech.intellispaces.framework.annotationprocessor.artifact.JavaArtifactContext;
import tech.intellispaces.framework.annotationprocessor.generator.TemplateBasedJavaArtifactGenerator;
import tech.intellispaces.framework.commons.action.Action;
import tech.intellispaces.framework.commons.type.TypeFunctions;
import tech.intellispaces.framework.core.common.ActionFunctions;
import tech.intellispaces.framework.core.common.NameFunctions;
import tech.intellispaces.framework.javastatements.statement.custom.CustomType;
import tech.intellispaces.framework.javastatements.statement.custom.MethodParam;
import tech.intellispaces.framework.javastatements.statement.custom.MethodStatement;
import tech.intellispaces.framework.javastatements.statement.reference.CustomTypeReference;
import tech.intellispaces.framework.javastatements.statement.reference.NamedTypeReference;
import tech.intellispaces.framework.javastatements.statement.reference.NonPrimitiveTypeReference;
import tech.intellispaces.framework.javastatements.statement.reference.TypeReference;
import tech.intellispaces.framework.javastatements.statement.reference.WildcardTypeReference;

import javax.annotation.processing.Generated;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static tech.intellispaces.framework.core.common.ActionFunctions.buildAppendSeparatorAction;

public abstract class AbstractGenerator extends TemplateBasedJavaArtifactGenerator {
  protected String generatedAnnotation;
  protected final JavaArtifactContext context = new JavaArtifactContext();

  public AbstractGenerator(CustomType annotatedType) {
    super(annotatedType);
    context.addImport(Generated.class);
  }

  @Override
  protected String canonicalName() {
    return context.generatedClassCanonicalName();
  }

  protected String sourceClassCanonicalName() {
    return annotatedType.canonicalName();
  }

  protected String sourceClassSimpleName() {
    if (annotatedType.isNested()) {
      return context.simpleNameOf(sourceClassCanonicalName());
    }
    return annotatedType.simpleName();
  }

  protected String generatedAnnotation() {
    if (generatedAnnotation == null) {
      generatedAnnotation = """
        @Generated(
          value = "%s",
          date = "%s",
          comments = "Generated with library %s"
        )""".formatted(
          this.getClass().getCanonicalName(),
          ZonedDateTime.now().format(ISO_OFFSET_DATE_TIME),
          TypeFunctions.getJavaLibraryName(this.getClass())
      );
    }
    return generatedAnnotation;
  }

  protected String getMethodSignature(MethodStatement method) {
    return getMethodSignature(method, method.name(), List.of());
  }

  protected String getMethodSignature(MethodStatement method, String methodName) {
    return getMethodSignature(method, methodName, List.of());
  }

  protected String getMethodSignature(MethodStatement method, String methodName, List<String> additionalParams) {
    var signature = new StringBuilder();

    if (!method.typeParameters().isEmpty()) {
      Action addCommaAction = buildAppendSeparatorAction(signature, ", ");
      signature.append("<");
      addCommaAction.execute();
      for (NamedTypeReference typeParam : method.typeParameters()) {
        signature.append(typeParam.actualDeclaration());
      }
      signature.append("> ");
    }

    if (method.returnType().isEmpty()) {
      signature.append("void");
    } else {
      TypeReference returnType = method.returnType().get();
      context.addImports(returnType.dependencyTypenames());
      signature.append(returnType.actualDeclaration());
      signature.append(" ");
      signature.append(methodName);
      signature.append("(");

      Action addCommaAction = buildAppendSeparatorAction(signature, ", ");
      for (String additionalParam : additionalParams) {
        addCommaAction.execute();
        signature.append(additionalParam);
      }
      for (MethodParam param : method.params()) {
        addCommaAction.execute();
        context.addImports(param.type().dependencyTypenames());
        signature.append(param.type().actualDeclaration());
        signature.append(" ");
        signature.append(param.name());
      }
      signature.append(")");

      String exceptions = method.exceptions().stream()
          .map(e -> e.asCustomTypeReference().orElseThrow().targetType())
          .peek(e -> context.addImport(e.canonicalName()))
          .map(e -> context.simpleNameOf(e.canonicalName()))
          .collect(Collectors.joining(", "));
      if (!exceptions.isEmpty()) {
        signature.append(" throws ").append(exceptions);
      }
    }
    return signature.toString();
  }

  protected String getHandleTypename(TypeReference type, Consumer<String> imports) {
    if (type.asPrimitiveTypeReference().isPresent()) {
      return type.asPrimitiveTypeReference().get().typename();
    } else if (type.asNamedTypeReference().isPresent()) {
      return type.asNamedTypeReference().get().name();
    } else if (type.asCustomTypeReference().isPresent()) {
      CustomTypeReference customTypeReference = type.asCustomTypeReference().get();
      CustomType targetType = customTypeReference.targetType();
      if (targetType.canonicalName().startsWith("java.lang.")) {
        return targetType.simpleName();
      } else {
        var sb = new StringBuilder();
        String canonicalName = NameFunctions.getObjectHandleClassCanonicalName(targetType.className());
        imports.accept(canonicalName);
        sb.append(context.simpleNameOf(canonicalName));
        if (!customTypeReference.typeArguments().isEmpty()) {
          sb.append("<");
          Action addCommaAction = ActionFunctions.buildAppendSeparatorAction(sb, ", ");
          for (NonPrimitiveTypeReference argType : customTypeReference.typeArguments()) {
            addCommaAction.execute();
            sb.append(getHandleTypename(argType, imports));
          }
          sb.append(">");
        }
        return sb.toString();
      }
    } else if (type.asWildcardTypeReference().isPresent()) {
      WildcardTypeReference wildcardTypeReference = type.asWildcardTypeReference().get();
      if (wildcardTypeReference.extendedBound().isPresent()) {
        return getHandleTypename(wildcardTypeReference.extendedBound().get(), imports);
      } else {
        throw new UnsupportedOperationException("Not implemented");
      }
    } else if (type.asArrayTypeReference().isPresent()) {
      TypeReference elementType = type.asArrayTypeReference().get().elementType();
      return getHandleTypename(elementType, imports) + "[]";
    } else {
      throw new UnsupportedOperationException("Unsupported type - " + type.actualDeclaration());
    }
  }
}
