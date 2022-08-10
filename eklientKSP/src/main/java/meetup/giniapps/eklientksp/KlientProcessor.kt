@file:OptIn(KspExperimental::class)

package meetup.giniapps.eklientksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import meetup.giniapps.eklientannotaions.EKlient
import meetup.giniapps.eklientannotaions.EKlientGet
import meetup.giniapps.eklientksp.Utils.validateURLRules
import okhttp3.OkHttpClient

class EKlientProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val gradleOptions: GradleOptions,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        return resolver.getSymbolsWithAnnotation(EKlient::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.INTERFACE }
            .toList()
            .onEach { userEKlientInterface: KSClassDeclaration ->
                val eklient: EKlient = userEKlientInterface.getAnnotationsByType(EKlient::class)
                    .first()
                    .validateURLRules()

                val funSpecs: EKlientFunctions = EKlientFunctions(
                    eklient,
                    userEKlientInterface,
                    requestToJson = {
                        Utils.networkGetCall(it, okHttpGenerativeClient(gradleOptions))
                    },
                    jsonToFile = { className, ktFile ->
                        codeGenerator.generateFiles(
                            className,
                            ktFile
                        )
                    }
                )

                val klientFileSpec: EKlientFile = EKlientFile(
                    eklient,
                    userEKlientInterface,
                    funSpecs
                )

                klientFileSpec.generate(codeGenerator)
            }
            .toList()
            .filter { false }
    }

    private fun okHttpGenerativeClient(
        options: GradleOptions,
    ) = OkHttpClient.Builder()
        .addInterceptor {
            /*TODO use options to modify request*/
            it.proceed(it.request())
        }
        .build()

    private fun CodeGenerator.generateFiles(
        className: ClassName,
        kt: String
    ) = createNewFile(
        Dependencies(false),
        className.packageName,
        className.simpleName
    ).write(kt.toByteArray())

}
