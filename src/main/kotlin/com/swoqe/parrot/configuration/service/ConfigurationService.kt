package com.swoqe.parrot.configuration.service

import aws.sdk.kotlin.services.cloudformation.model.Parameter
import com.swoqe.parrot.aws.freemarker.Ec2DockerEntity
import com.swoqe.parrot.aws.TemplateHolder
import com.swoqe.parrot.configuration.dto.ConfigurationRoot
import com.swoqe.parrot.configuration.dto.ServiceType

object ConfigurationService {

//    private val dashboard = TemplateHolder.javaClass.getResource("/cf-templates/pieces/dashboard.yml")?.readText().toString()

    suspend fun deploy(configuration: ConfigurationRoot) {
        val templateParams: MutableList<Parameter> = mutableListOf()
        var serviceParams: Map<String, String> = mutableMapOf()
        configuration.aws.services.forEach { service ->
            serviceParams = serviceParams + (service.properties ?: mapOf())
        }

        val templateType = defineTemplateType(configuration)

        val template: String?

        when (templateType) {
            TemplateType.STANDALONE -> {
                template = TemplateHolder.ec2Docker(
                    Ec2DockerEntity(
                        configuration.docker.image,
                        configuration.docker.innerPort,
                        parseEnvVariables(configuration)
                    )
                )
            }
            TemplateType.STANDALONE_VPC -> {
                template = TemplateHolder.ec2Vpc(
                    Ec2DockerEntity(
                        configuration.docker.image,
                        configuration.docker.innerPort,
                        parseEnvVariables(configuration)
                    )
                )
            }
            TemplateType.ALB_SCALING_VPC -> {
                template = TemplateHolder.albSgVpc(
                    Ec2DockerEntity(
                        configuration.docker.image,
                        configuration.docker.innerPort,
                        parseEnvVariables(configuration)
                    ),
                    serviceParams
                )
            }
        }

        if (configuration.aws.ec2ImageId != null) {
            templateParams.add(Parameter{
                parameterKey = "AmiID"
                parameterValue = configuration.aws.ec2ImageId
            })
        }
        CloudformationService.deployStack(configuration.aws.region, configuration.name, template, templateParams)
    }

    private fun parseEnvVariables(configuration: ConfigurationRoot): String {
        val envBuilder = StringBuilder()
        configuration.docker.environment.forEach { env -> envBuilder.append("-e ${env.name}=${env.value} ") }
        return envBuilder.toString()
    }

    private fun defineTemplateType(configuration: ConfigurationRoot) : TemplateType {
        val vpc = configuration.aws.services.any { service -> service.type == ServiceType.VPC }
        val alb = configuration.aws.services.any { service -> service.type == ServiceType.ALB }
        val sg = configuration.aws.services.any { service -> service.type == ServiceType.SG }

        return if (vpc && alb && sg) {
            TemplateType.ALB_SCALING_VPC
        } else if (vpc && !(alb || sg)) {
            TemplateType.STANDALONE_VPC
        } else {
            TemplateType.STANDALONE
        }
    }

}
