package com.kbalazsworks.common.native_build_module.services

import com.kbalazsworks.common.io_module.services.FileService
import com.kbalazsworks.common.native_build_module.exceptions.RuntimeHintsReflection
import com.kbalazsworks.common.native_build_module.value_objects.ReflectionClassList
import com.kbalazsworks.common.templating_module.services.MustacheService
import org.springframework.stereotype.Service
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

@Service
class RuntimeHintsReflectionGenerator(
    private val mustacheService: MustacheService,
    private val fileService: FileService
) {

    @Throws(IOException::class, RuntimeHintsReflection::class)
    fun generate(destinationFile: String, packageNames: List<String>, classes: List<Class<*>>) {
        val newCode = mustacheService
            .execute(TEMPLATE, ReflectionClassList(getClassNamesInPackages(packageNames, classes)))

        val oldCode = fileService.readString(destinationFile)

        fileService.saveString(destinationFile, newCode)

        require(oldCode == newCode) { "New ReflectionConfiguration generated, please restart the application!" }
    }

    companion object {
        private const val TEMPLATE = "templates/RuntimeHintsReflectionTemplate.mustache"

        private fun getClassNamesInPackages(packageNames: List<String>, classes: List<Class<*>>): List<String> =
            classes.map { "${it.name}::class" } +
                    packageNames.flatMap { getClassNamesInPackage(it) }

        private fun getClassNamesInPackage(packageName: String) = runCatching {
            Files
                .walk(Paths.get("src/main/java/${packageName.replace('.', '/')}"))
                .use { paths ->
                    paths
                        .filter(Files::isRegularFile)
                        .filter { it.toString().endsWith(".java") }
                        .map { "$packageName.${it.fileName.toString().removeSuffix(".java")}.class" }
                        .toList()
                }
        }.getOrDefault(emptyList())
    }
}
