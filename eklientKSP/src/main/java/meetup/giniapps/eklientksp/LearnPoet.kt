package meetup.giniapps.eklientksp

import com.squareup.kotlinpoet.*
import kotlinx.serialization.Serializable

fun main() {
    LearnPoet().generateClass()
        .also(::print)
}

class LearnPoet {

    fun generateClass() = TypeSpec.classBuilder("Counter")
        .addFunction(generateFunction("increment", "counter++"))
        .addInitializerBlock(generateBlock(""))
        .addProperty(generateConstructorProperty("counter", Int::class.asTypeName()))
        .primaryConstructor(generateConstructor(generateParameter("counter", Int::class.asTypeName())))
        .addAnnotation(generateAnnotation(Serializable::class.asClassName()))
        .build()

    private fun generateBlock(codeAsString: String): CodeBlock = CodeBlock
        .builder()
        .add(CodeBlock.of(codeAsString))
        .build()

    private fun generateFunction(funName: String, funCode: String): FunSpec = FunSpec
        .builder(funName)
        .addCode(funCode)
        .build()

    private fun generateParameter(
        propertyName: String,
        type: TypeName,
        kModifiers: List<KModifier> = listOf()
    ): ParameterSpec = ParameterSpec(propertyName, type, kModifiers)
        .toBuilder()
        .build()

    private fun generateConstructor(parameterSpec: ParameterSpec): FunSpec = FunSpec
        .constructorBuilder()
        .addParameter(parameterSpec)
        .build()

    private fun generateConstructorProperty(
        propertyName: String,
        type: TypeName,
        kModifiers: List<KModifier> = listOf()
    ): PropertySpec = PropertySpec
        .builder(propertyName, type, kModifiers)
        .initializer("counter")
        .build()

    private fun generateAnnotation(className: ClassName): AnnotationSpec = AnnotationSpec
        .builder(className)
        .build()


}