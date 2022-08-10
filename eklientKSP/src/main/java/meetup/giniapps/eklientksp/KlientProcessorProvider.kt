package meetup.giniapps.eklientksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class EKlientProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val gradleOptions = processorGradleOptions(environment.options)
        return EKlientProcessor(environment.codeGenerator, environment.logger, gradleOptions)
    }

    private fun processorGradleOptions(options: Map<String, String>): GradleOptions {
        return GradleOptions()
    }


}