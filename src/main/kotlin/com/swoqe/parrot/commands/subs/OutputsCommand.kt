package com.swoqe.parrot.commands.subs

import aws.sdk.kotlin.runtime.InternalSdkApi
import aws.sdk.kotlin.runtime.region.resolveRegion
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.swoqe.parrot.configuration.service.CloudformationService
import com.swoqe.parrot.configuration.util.JacksonYamlMapper
import kotlinx.coroutines.runBlocking
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import java.util.concurrent.Callable

@Command(
    name = "outputs",
    description = ["Describes outputs of the stack with the given name"]
)
class OutputsCommand : Callable<Int> {

    private val objectMapper = JacksonYamlMapper.instance.writerWithDefaultPrettyPrinter()

    @Parameters(description = ["Name of the stack to be described"])
    lateinit var stackName: String

    @OptIn(InternalSdkApi::class)
    @Option(names = ["-r", "--region"], required = false, description = ["Region where the stack is located"])
    var region: String = runBlocking { resolveRegion() }

    override fun call(): Int {
        runBlocking {
            println(objectMapper.writeValueAsString(CloudformationService.getStackOutputsByName(region, stackName)))
        }
        return 0
    }
}