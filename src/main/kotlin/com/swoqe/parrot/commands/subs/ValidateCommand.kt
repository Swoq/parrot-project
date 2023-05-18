package com.swoqe.parrot.commands.subs

import com.swoqe.parrot.commands.ParrotCommand
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.io.File
import java.util.concurrent.Callable
import kotlin.system.exitProcess

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
        file.walk().forEach {
            println(it.name)
        }
        return 0
    }
}