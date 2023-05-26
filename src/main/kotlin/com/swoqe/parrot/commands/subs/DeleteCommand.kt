package com.swoqe.parrot.commands.subs

import aws.sdk.kotlin.runtime.InternalSdkApi
import aws.sdk.kotlin.runtime.region.resolveRegion
import aws.smithy.kotlin.runtime.retries.toResult
import com.swoqe.parrot.configuration.service.CloudformationService
import kotlinx.coroutines.runBlocking
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import java.util.concurrent.Callable

@Command(
    name = "delete",
    description = ["Initialize deleting of the cloudformation stack"]
)
class DeleteCommand : Callable<Int> {

    @Parameters(index = "0", description = ["stack name to be deleted"])
    lateinit var stackName: String

    @OptIn(InternalSdkApi::class)
    @Option(names = ["-r", "--region"], required = false, description = ["region to delete stack from"])
    var region: String = runBlocking { resolveRegion() }

    @Option(names = ["-d", "--detach"], description = ["Run as a demon process"])
    var demon: Boolean = true

    override fun call(): Int {
        runBlocking {
            CloudformationService.deleteStackByName(region, stackName)
            println("Deletion has been initialized for stack with name: $stackName")
            if (!demon) {
                println("\tDeleting has been initialized...")
                println("\tWaiting stack to finish deleting...")

                val stack = CloudformationService.waitUntilStackDeleted(region, stackName)

                println("\tStack ${stack.toResult().getOrThrow().stacks?.first()?.stackName} has successfully been deleted")
            }
        }
        return 0
    }
}