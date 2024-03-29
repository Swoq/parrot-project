package com.swoqe.parrot.commands.subs

import aws.sdk.kotlin.runtime.InternalSdkApi
import aws.sdk.kotlin.runtime.region.resolveRegion
import aws.sdk.kotlin.services.cloudformation.model.StackStatus
import aws.smithy.kotlin.runtime.retries.toResult
import com.mifmif.common.regex.Generex
import com.swoqe.parrot.configuration.dto.ConfigurationRoot
import com.swoqe.parrot.configuration.service.CloudformationService
import com.swoqe.parrot.configuration.service.ConfigurationService
import com.swoqe.parrot.configuration.service.ValidationService
import com.swoqe.parrot.configuration.util.JacksonYamlMapper
import kotlinx.coroutines.runBlocking
import picocli.CommandLine.*
import java.io.File
import java.net.URI
import java.nio.file.Paths
import java.util.concurrent.Callable


@Command(
    name = "deploy",
    description = ["Deploys the configuration file to the new cloud infrastructure"]
)
class DeployCommand : Callable<Int> {

    @Parameters(index = "0", description = ["Configuration file to load service description from"])
    lateinit var path: String

    @OptIn(InternalSdkApi::class)
    @Option(names = ["-r", "--region"], required = false, description = ["Region to deploy stack in"])
    var region: String = runBlocking { resolveRegion() }

    @Option(names = ["-d", "--detached"], required = false, description = ["Run as a demon process"])
    var demon: Boolean = false

    private val file : File by lazy {
        if (path.startsWith('/')) File(path) else File(System.getenv("OLDPWD"), path)
    }

    private val generex = Generex("[a-zA-Z][-a-zA-Z0-9]*")
    private val objectMapper = JacksonYamlMapper.instance.writerWithDefaultPrettyPrinter()

    override fun call(): Int {
        runCatching {
            val schema = ValidationService.javaClass.getResource("/schema/schema.json")
            val report = ValidationService.validate(file, schema?.readText() ?: "")

            if (report == null || !report.isSuccess) {
                print(report)
                return 0
            }

            val configurationRoot = JacksonYamlMapper.instance.readValue(file, ConfigurationRoot::class.java)
            configurationRoot.aws.region = configurationRoot.aws.region ?: region
            configurationRoot.name = configurationRoot.name ?: generex.random(10, 11)
            println("Successfully loaded config from file: ${file.name}")
            runBlocking {
                ConfigurationService.deploy(configurationRoot)
                println("Creation has been initialized with name: ${configurationRoot.name}")
                if (!demon) {
                    println("\tDeployment's been initialized...")
                    println("\tWaiting stack to finish deployment...")

                    val stacks = CloudformationService.waitUntilStackDeployed(region, configurationRoot.name)
                    val stack = stacks.toResult().getOrThrow().stacks?.first()
                    if (stack?.stackStatus == StackStatus.CreateComplete) {
                        println(" === Outputs: \n${objectMapper.writeValueAsString(stack.outputs)}")
                    } else {
                        print(" === Check out stack events to find the reason of failure")
                    }
                }
            }
        }.onFailure { exception ->
            println(exception)
        }
        return 0
    }



}
