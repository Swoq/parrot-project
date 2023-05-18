package com.swoqe.parrot.configuration.service

import aws.sdk.kotlin.services.cloudformation.CloudFormationClient
import aws.sdk.kotlin.services.cloudformation.model.CreateStackRequest
import aws.sdk.kotlin.services.cloudformation.model.DeleteStackRequest
import aws.sdk.kotlin.services.cloudformation.model.OnFailure
import aws.sdk.kotlin.services.cloudformation.model.Parameter
import com.swoqe.parrot.aws.Ec2Docker
import com.swoqe.parrot.aws.TemplateHolder
import com.swoqe.parrot.configuration.dto.ConfigurationRoot

object ConfigurationService {

    suspend fun deploy(configuration: ConfigurationRoot) {
        val ec2DockerTemplate = TemplateHolder.ec2Docker(
            Ec2Docker(
                configuration.docker.image,
                configuration.docker.innerPort,
                configuration.aws.ec2ImageId,
                configuration.docker.dbEndpoint,
                configuration.docker.dbUser,
                configuration.docker.dbPassword
            )
        )
        CloudformationService.deployStack(configuration.aws.region, configuration.name, ec2DockerTemplate)
    }

}
