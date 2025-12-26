package com.common.templating_module.services

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import org.springframework.stereotype.Service
import java.io.IOException
import java.io.StringWriter

@Service
class MustacheService {
    @Throws(IOException::class)
    fun execute(template: String, templateData: Any): String {
        val mustacheFactory: MustacheFactory = DefaultMustacheFactory()
        val mustache = mustacheFactory.compile(template)
        val writer = StringWriter()
        mustache.execute(writer, templateData).flush()

        return writer.toString()
    }
}
