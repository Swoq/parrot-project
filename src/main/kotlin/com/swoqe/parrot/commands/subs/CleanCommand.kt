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
    name = "clean",
    description = ["Starts deleting process of all stacks in a region"]
)
class CleanCommand : Callable<Int> {

    @OptIn(InternalSdkApi::class)
    @Option(names = ["-r", "--region"], required = false, description = ["Region where the stacks is located"])
    var region: String = runBlocking { resolveRegion() }

    override fun call(): Int {
        println("Starting deleting process...")
        runBlocking {
            val deleteAllStackInRegion = CloudformationService.deleteAllStackInRegion(region)
            println("Deletion has been initialized for stacks: \n")
            deleteAllStackInRegion.forEach { name -> println(name) }
        }
        return 0
    }
}