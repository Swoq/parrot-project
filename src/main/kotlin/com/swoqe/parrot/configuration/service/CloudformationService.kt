package com.swoqe.parrot.configuration.service

import aws.sdk.kotlin.services.cloudformation.CloudFormationClient
import aws.sdk.kotlin.services.cloudformation.model.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object CloudformationService {

    private val objectMapper = jacksonObjectMapper().writerWithDefaultPrettyPrinter()

    suspend fun deployStack(region: String, stackNameVal: String, template: String) {
        val request = CreateStackRequest {
            stackName = stackNameVal
            templateBody = template
            onFailure = OnFailure.Rollback
        }

        CloudFormationClient { this.region = region }.use { cfClient ->
            val createStackResponse = cfClient.createStack(request)
            println("Stack - $stackNameVal | Creation has been initialized with stack-id:\n ${createStackResponse.stackId}")
        }
    }

    suspend fun deleteStackByName(region: String, stackNameVal: String) {
        CloudFormationClient { this.region = region }.use { cfClient ->
            cfClient.deleteStack(DeleteStackRequest {
                stackName = stackNameVal
            })
            println("The AWS CloudFormation stack was successfully deleted!")
        }
    }

    suspend fun describeAllStacks(region: String) {
        CloudFormationClient { this.region = region }.use { cfClient ->
            val stacksResponse: DescribeStacksResponse = cfClient.describeStacks(DescribeStacksRequest {})
            stacksResponse.stacks?.forEach { stack ->
                println(objectMapper.writeValueAsString(stack))
            }
        }
    }

    suspend fun describeStackByName(region: String, name: String?) {
        CloudFormationClient { this.region = region }.use { cfClient ->
            val stacksResponse: DescribeStacksResponse = cfClient.describeStacks(DescribeStacksRequest {stackName = name})
            stacksResponse.stacks?.forEach { stack ->
                println(objectMapper.writeValueAsString(stack))
            }
        }
    }
}