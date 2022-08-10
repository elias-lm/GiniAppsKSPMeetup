package meetup.giniapps.eklientksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import meetup.giniapps.eklientannotaions.EKlient
import okhttp3.OkHttpClient
import java.net.URL
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class EKlientFile(
    private val eklient: EKlient,
    private val userAnnotatedInterface: KSClassDeclaration,
    private val funcs: EKlientFunctions
) {

    private val klientFileName = "${userAnnotatedInterface.simpleName.asString()}EKlient"

    private val klientFactoryClassName = ClassName(
        userAnnotatedInterface.packageName.asString(),
        "${klientFileName}Factory"
    )

    private val klientContextClassName = ClassName(
        userAnnotatedInterface.packageName.asString(),
        "${userAnnotatedInterface.simpleName.asString()}Context"
    )

    fun generate(codeGenerator: CodeGenerator) {
        FileSpec.builder(userAnnotatedInterface.packageName.asString(), klientFileName)
            .addType(klientContext())
            .addType(apiDelegateFactory())
            .addProperty(klientContextPropertyExtension())
            .addFunction(klientDelegate())
            .addImport("meetup.giniapps.eklient", "Utils.call")
            .build()
            .writeTo(codeGenerator, false)
    }

    private fun apiDelegateFactory(): TypeSpec {

        fun getValueFunc(): FunSpec {
            val simpleName = userAnnotatedInterface.simpleName.asString()
            return FunSpec.builder("getValue")
                .addModifiers(KModifier.OVERRIDE)
                .addCode(
                    CodeBlock.of(
//                        "if(property is $simpleName) {\n" +
                        "        return object : $simpleName {}\n" +
                                "            .apply { context = ${klientContextClassName.simpleName}() }\n"
//                                "      } else {\n" +
//                                "        throw IllegalStateException(\"${klientFactoryClassName.simpleName} support only $simpleName\")\n" +
//                                "      }"
                    )
                )
                .addParameter("thisRef", Any::class.asTypeName().copy(nullable = true))
                .addParameter(
                    "property",
                    KProperty::class.asTypeName()
                        .parameterizedBy(TypeVariableName("*"))
                )
                .returns(userAnnotatedInterface.toClassName())
                .build()
        }

        return TypeSpec.classBuilder(klientFactoryClassName)
            .addSuperinterface(
                ReadOnlyProperty::class.asTypeName().parameterizedBy(
                    Any::class.asTypeName().copy(nullable = true),
                    userAnnotatedInterface.toClassName()
                )
            )
            .addFunction(getValueFunc())
            .build()
    }

    private fun klientDelegate(): FunSpec {
        return FunSpec.builder("invoke")
            .receiver(userAnnotatedInterface.toClassName())
            .addModifiers(KModifier.OPERATOR, KModifier.SUSPEND)
            .addParameter(
                "func", LambdaTypeName.get(
                    receiver = klientContextClassName,
                    returnType = Unit::class.asTypeName(),
                ).copy(suspending = true)
            )
            .addCode("func(context)")
            .build()
    }

    private fun klientContextPropertyExtension(): PropertySpec {
        return PropertySpec.builder("context", klientContextClassName, KModifier.PRIVATE)
            .mutable()
            .delegate("kotlin.properties.Delegates.notNull()")
            .build()
    }

    private fun klientContext(): TypeSpec {
        return TypeSpec.classBuilder(klientContextClassName)
            .addProperty(urlProperty(eklient.url))
            .addProperty(okhttpProperty())
            .apply {
                funcs.forEach { fs ->
                    addFunction(fs)
                }
            }
            .build()
    }

    private fun okhttpProperty(): PropertySpec {
        return PropertySpec.builder("okhttp", OkHttpClient::class, KModifier.PRIVATE)
            .initializer("OkHttpClient.Builder().build()")
            .build()
    }

    private fun urlProperty(url: String): PropertySpec {
        return PropertySpec.builder("url", URL::class)
            .addModifiers(KModifier.PRIVATE)
            .initializer("URL(\"$url\")")
            .build()
    }
}