package com.common.io_module.services

import org.springframework.stereotype.Service
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

@Service
class FileService {
    @Throws(IOException::class)
    fun saveString(destinationFile: String, content: String) {
        Files.writeString(Paths.get(destinationFile), content)
    }

    @Throws(IOException::class)
    fun readString(sourceFile: String) = Files.readString(Paths.get(sourceFile))
}
