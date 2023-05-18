package com.swoqe.parrot.commands.subs

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.swoqe.parrot.configuration.dto.ConfigurationRoot
import com.swoqe.parrot.configuration.service.ConfigurationService
import com.swoqe.parrot.configuration.util.JacksonYamlMapper
import kotlinx.coroutines.runBlocking
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.io.File
import java.util.concurrent.Callable


@Command(
    name = "deploy",
    description = ["Deploys the configuration file to the new cloud infrastructure"]
)
class DeployCommand : Callable<Int> {

    @Parameters(index = "0", description = ["The Parrot configuration file to be validated"])
    lateinit var path: String

    private val file : File by lazy {
        if (path.startsWith('/')) File(path) else File(System.getenv("OLDPWD"), path)
    }

    override fun call(): Int {
        try {

            val configurationRoot = JacksonYamlMapper.instance.readValue(file, ConfigurationRoot::class.java)

            println("Successfully loaded config file: ${file.name}")
            runBlocking { ConfigurationService.deploy(configurationRoot) }
        } catch (exception: MismatchedInputException) {
            println("Could not read YAML file!")
            println(exception.message)
        }
        return 0
    }



}
