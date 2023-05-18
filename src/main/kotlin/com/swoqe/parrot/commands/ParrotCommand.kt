package com.swoqe.parrot.commands

import com.swoqe.parrot.commands.subs.DeleteCommand
import com.swoqe.parrot.commands.subs.DeployCommand
import com.swoqe.parrot.commands.subs.StatusCommand
import com.swoqe.parrot.commands.subs.ValidateCommand
import picocli.CommandLine
import picocli.CommandLine.Command
import java.util.concurrent.Callable
import kotlin.system.exitProcess

@Command(
    name = "parrot",
    mixinStandardHelpOptions = true,
    version = ["parrot 4.0"],
    description = ["Simple CLI Tool which implements Infrastructure As Code (IAC) paradigm in AWS Cloud. " +
            "Accepts YAML file and deploy provided services around a Docker container."],
    subcommands = [
        ValidateCommand::class,
        DeployCommand::class,
        DeleteCommand::class,
        StatusCommand::class
    ]
)
class ParrotCommand : Callable<Int> {

    override fun call(): Int {
        return 0
    }
}

fun main(args: Array<String>) : Unit = exitProcess(CommandLine(ParrotCommand()).execute(*args))