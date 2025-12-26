package com.common.native_build_module.services

import com.common.io_module.services.FileService
import com.common.native_build_module.exceptions.RuntimeHintsReflection
import com.common.native_build_module.value_objects.ReflectionClassList
import com.common.templating_module.services.MustacheService
import org.springframework.stereotype.Service
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Service
class RuntimeHintsReflectionGenerator
    (private val mustacheService: MustacheService, private val fileService: FileService) {
    @Throws(IOException::class, RuntimeHintsReflection::class)
    fun generate(destinationFile: String, packageNames: List<String>, classes: List<Class<*>>) {
        val classNames: List<String> = getClassNamesInPackages(packageNames, classes)

        val newCode = mustacheService.execute(
            "templates/RuntimeHintsReflectionTemplate.mustache",
            ReflectionClassList(classNames)
        )

        val oldCode = fileService.readString(destinationFile)
        fileService.saveString(destinationFile, newCode)

        if (oldCode != newCode) {
            throw RuntimeHintsReflection("New ReflectionConfiguration generated, please restart the application!")
        }
    }

    companion object {
        private fun getClassNamesInPackages(packageNames: List<String>, classes: List<Class<*>>): List<String> {
            val fqns: MutableList<String> = mutableListOf()

            classes.stream().map<String> { c: Class<*>? -> c!!.getName() + "::class" }
                .forEach { e: String -> fqns.add(e) }

            packageNames.stream()
                .map<List<String>> { packageName: String -> Companion.getClassNamesInPackage(packageName) }
                .flatMap<String?> { obj: List<String> -> obj.stream() }
                .forEach { e: String -> fqns.add(e) }

            return fqns
        }

        private fun getClassNamesInPackage(packageName: String): List<String> {
            val path = "src/main/java/" + packageName.replace('.', '/')
            val classNames: MutableList<String> = mutableListOf()

            try {
                Files.walk(Paths.get(path)).use { paths ->
                    paths
                        .filter { path: Path -> Files.isRegularFile(path) }
                        .filter { p: Path -> p.toString().endsWith(".java") }
                        .forEach { p: Path ->
                            classNames.add(
                                packageName + "." + p.getFileName().toString().replace(".java", ".class")
                            )
                        }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return classNames
        }
    }
}
