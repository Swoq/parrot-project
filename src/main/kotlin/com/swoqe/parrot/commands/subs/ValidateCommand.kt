package com.swoqe.parrot.commands.subs

import aws.sdk.kotlin.runtime.InternalSdkApi
import aws.sdk.kotlin.runtime.region.resolveRegion
import com.swoqe.parrot.configuration.dto.ConfigurationRoot
import com.swoqe.parrot.configuration.service.ValidationService
import com.swoqe.parrot.configuration.util.JacksonYamlMapper
import kotlinx.coroutines.runBlocking
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.io.File
import java.util.concurrent.Callable

@Command(
    name = "validate",
    description = ["Validates a Parrot YAML configuration file on structure criteria"]
)
class ValidateCommand : Callable<Int> {

    @Parameters(index = "0", description = ["The Parrot configuration file to be validated"])
    lateinit var path: String

    private val file : File by lazy {
        if (path.startsWith('/')) File(path) else File(System.getenv("OLDPWD"), path)
    }

    override fun call(): Int {
        val schema = ValidationService.javaClass.getResource("/schema/schema.json")
        val report = ValidationService.validate(file, schema?.readText() ?: "")

        if (report == null || !report.isSuccess) {
            println(report)
            return 0
        } else {
            println("Structure validation has been successful...")
            println("Configuration object: ")
        }

        val configurationRoot = JacksonYamlMapper.instance.readValue(file, ConfigurationRoot::class.java)
        println(configurationRoot)
        return 0
    }
}