package com.swoqe.parrot.commands.subs

import aws.sdk.kotlin.runtime.InternalSdkApi
import aws.sdk.kotlin.runtime.region.resolveRegion
import aws.sdk.kotlin.services.cloudformation.model.ResourceStatus
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.swoqe.parrot.configuration.service.CloudformationService
import com.swoqe.parrot.configuration.util.JacksonYamlMapper
import kotlinx.coroutines.runBlocking
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.util.concurrent.Callable

@Command(
    name = "status",
    description = ["Describes events of the stack with the given name"]
)
class StatusCommand : Callable<Int> {

    private val objectMapper = JacksonYamlMapper.instance.writerWithDefaultPrettyPrinter()

    @Option(names = ["-n", "--name"], required = false, description = ["Name of the stack to be described"])
    var stackName: String? = null

    @Option(names = ["-a", "--all"], required = false, description = ["Print all available information about stack"])
    var allInformation: Boolean = false

    @OptIn(InternalSdkApi::class)
    @Option(names = ["-r", "--region"], required = false, description = ["Region to deploy stack in"])
    var region: String = runBlocking { resolveRegion() }

    override fun call(): Int {
        runBlocking {

            if (stackName == null) {
                val stacks = CloudformationService.describeAllStacks(region)
                if (allInformation) {
                    println(objectMapper.writeValueAsString(stacks))
                } else {
                    System.out.printf("%-40s %s\n",  "Stack Name ", ": Stack Status")
                    println("==========================================================")
                    stacks?.forEach { stack ->
                        System.out.printf("%-40s %s\n",  "${stack.stackName} ", ": ${stack.stackStatus}")
                    }

                }

            } else {
                val resources = CloudformationService.describeStackResourcesByName(region, stackName)
                System.out.printf("%-60s %s\n",  "Time : Resource Name ", ": Resource Status")
                println("==========================================================================")
                resources?.forEach { resource ->
                    System.out.printf("%-60s %s\n",  "${resource.timestamp} : ${resource.logicalResourceId}", ": ${resource.resourceStatus}")
                    if (resource.resourceStatus == ResourceStatus.CreateFailed) {
                        println("\t Error: ${resource.resourceStatusReason?.split(";")?.joinToString("\n\t")}")
                    }
                }
            }
        }
        return 0
    }
}