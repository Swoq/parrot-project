package com.swoqe.parrot.commands.subs

import aws.smithy.kotlin.runtime.util.asyncLazy
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
    name = "delete",
    description = ["Initialize deleting of the cloudformation stack"]
)
class DeleteCommand : Callable<Int> {

    @Parameters(index = "0", description = ["Stack name to be deleted"])
    lateinit var stackName: String

    @Option(names = ["-r", "--region"], required = true, description = ["Region to delete stack from"])
    lateinit var region: String

    override fun call(): Int {
        runBlocking { CloudformationService.deleteStackByName(region, stackName) }
        return 0
    }
}