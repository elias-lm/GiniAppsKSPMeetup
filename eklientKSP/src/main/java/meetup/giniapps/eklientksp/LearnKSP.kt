package meetup.giniapps.eklientksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType

class LearnKSP {

    annotation class Learn(val someData: String)

    @Learn(someData = "Oh Ya!")
    class Example(val prop: String = "") {
        fun doSomething() {
            println("Doing something")
        }
    }

    @OptIn(KspExperimental::class)
    fun extractAnnotationData(classDeclaration: KSClassDeclaration /*== Example*/) {
        //Option 1
        classDeclaration
            .getAnnotationsByType(Learn::class)
            .forEach {
                val data = it.someData
            }

        //Option 2
        classDeclaration
            .annotations
            .filter { it.shortName.asString() == Learn::class.simpleName!! }
            .forEach {
                val data = it.arguments.first().value
            }
    }

    fun extractProperties(classDeclaration: KSClassDeclaration /*== Example*/) {
        classDeclaration
            .getDeclaredProperties()
            .forEach {
                val propName = it.simpleName.asString()
                val propType: KSType = it.type.resolve()
            }
    }

    fun extractFunctionParameters(classDeclaration: KSClassDeclaration /*== Example*/) {
        classDeclaration
            .getAllFunctions()
            .forEach {
                it.parameters.forEach {
                    val paramName = it.name
                    val paramType: KSType = it.type.resolve()
                }
            }
    }
}