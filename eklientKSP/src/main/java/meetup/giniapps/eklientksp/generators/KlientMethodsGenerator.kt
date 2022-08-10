package meetup.giniapps.eklientksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import meetup.giniapps.eklientannotaions.EKlient
import meetup.giniapps.eklientannotaions.EKlientGet
import meetup.giniapps.eklientannotaions.EKlientPost
import meetup.giniapps.eklientksp.Utils.validatePathRules
import meetup.giniapps.eklientksp.eklient.EKlientMethods
import meetup.giniapps.eklientksp.eklient.toEKlientMethod
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import wu.seal.jsontokotlin.library.JsonToKotlinBuilder
import wu.seal.jsontokotlin.model.TargetJsonConverter

@OptIn(KspExperimental::class)
class EKlientFunctions(
    eklient: EKlient,
    userAnnotatedInterface: KSClassDeclaration,
    requestToJson: (Request) -> String,
    jsonToFile: (className: ClassName, ktFile: String) -> Unit
) : ArrayList<FunSpec>() {

    private val jsonToKotlinBuilder = JsonToKotlinBuilder().apply {
        setAnnotationLib(TargetJsonConverter.Serializable)
    }

    init {
        val klientGets = userAnnotatedInterface.getAnnotationsByType(EKlientGet::class)
            .onEach { it.validatePathRules() }
        val klientPosts = userAnnotatedInterface.getAnnotationsByType(EKlientPost::class)
            .onEach { it.validatePathRules() }

        (klientGets + klientPosts)
            .toList()
            .mapToExtractedAnnotationData(userAnnotatedInterface.packageName)
            .generateReturnTypes(
                baseUrl = eklient.url,
                requestToJson = requestToJson,
                jsonToFile = jsonToFile,
            )
            .mapToFunSpec()
            .forEach(::add)
    }

    private fun <T : Annotation> List<T>.mapToExtractedAnnotationData(
        packageName: KSName,
    ): List<ExtractedAnnotationData> = map { klientMethod ->
        val (path, functionName) = klientMethod.extractAnnotationData()
        ExtractedAnnotationData(
            relativePath = path,
            functionName = functionName,
            returnType = "${functionName.value}Response".pathToClassName(packageName),
            method = klientMethod.toEKlientMethod()
        )
    }

    private fun List<ExtractedAnnotationData>.mapToFunSpec(): List<FunSpec> {
        return map { data ->
            val methodBody = "return okhttp.call(\"\${url}${data.relativePath.value}\") {\n" +
                    "    kotlinx.serialization.json.Json.decodeFromString(${data.returnType}.serializer(), it)\n" +
                    "  }"

            FunSpec.builder("${data.functionName.value}")
                .addModifiers(KModifier.PUBLIC, KModifier.SUSPEND)
                .addCode(methodBody)
                .returns(data.returnType)
                .build()
        }
    }

    /* T should represent EKlient http method (ex: EKlientGet) */
    private fun List<ExtractedAnnotationData>.generateReturnTypes(
        baseUrl: String,
        requestToJson: (Request) -> String,
        jsonToFile: (className: ClassName, ktFile: String) -> Unit
    ): List<ExtractedAnnotationData> = onEach { data ->

        fun makeRequest(relativePath: String): Request = Request.Builder()
            .run {
                when (data.method) {
                    is EKlientMethods.GET -> get()
                    is EKlientMethods.POST -> post(/*TODO*/"".toRequestBody())
                }
            }
            .url("$baseUrl${relativePath}")
            .build()

        val json: String = requestToJson(makeRequest(data.relativePath.value))
        val jsonAsPnko = jsonToKotlinBuilder //Plain new kotlin object
            .setPackageName(data.returnType.packageName)
            .build(json, data.returnType.simpleName)
        jsonToFile(data.returnType, jsonAsPnko)
    }

    private fun <T : Annotation> T.extractAnnotationData(): Pair<Path, FunctionName> {

        fun T.annotationToPath(): String? {
            return when (this) {
                is EKlientGet -> this.path
                is EKlientPost -> this.path
                else -> null
            }
        }

        fun T.annotationToFunctionName(): String? {
            return when (this) {
                is EKlientGet ->
                    if (this.functionName == "")
                        "get" + this.path.pathToName()
                    else this.functionName
                is EKlientPost ->
                    if (this.functionName == "")
                        "post" + this.path.pathToName()
                    else this.functionName
                else -> null
            }
        }

        return Path(annotationToPath()!!) to FunctionName(annotationToFunctionName()!!)
    }

    private fun String.pathToClassName(packageName: KSName): ClassName {
        return ClassName(packageName.asString(), pathToName())
    }

    private fun String.pathToName(): String {
        return replace("-", "")
            .split("/")
            .joinToString("") {
                it.replaceFirstChar { c -> c.uppercase() }
            }
    }

    data class ExtractedAnnotationData(
        val relativePath: Path,
        val functionName: FunctionName,
        val returnType: ClassName,
        val method: EKlientMethods
    )

    @JvmInline
    value class Path(val value: String)

    @JvmInline
    value class FunctionName(val value: String)
}