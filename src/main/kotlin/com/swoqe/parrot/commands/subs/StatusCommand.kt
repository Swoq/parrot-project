package com.swoqe.parrot.commands.subs

import com.swoqe.parrot.commands.ParrotCommand
import com.swoqe.parrot.configuration.service.CloudformationService
import kotlinx.coroutines.runBlocking
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import java.io.File
import java.util.concurrent.Callable
import kotlin.system.exitProcess

@Command(
    name = "status",
    description = ["Describes events of the stack with the given name"]
)
class StatusCommand : Callable<Int> {

    @Option(names = ["-n", "--name"], required = false, description = ["Name of the stack to be described"])
    var stackName: String? = null

    @Option(names = ["-r", "--region"], required = true, description = ["Region to delete stack from"])
    lateinit var region: String

    override fun call(): Int {
        runBlocking {
            if (stackName == null) {
                CloudformationService.describeAllStacks(region)
            } else {
                CloudformationService.describeStackByName(region, stackName)
            }
        }
        return 0
    }
}