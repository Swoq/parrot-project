package com.swoqe.parrot.configuration.service

import aws.sdk.kotlin.services.cloudformation.CloudFormationClient
import aws.sdk.kotlin.services.cloudformation.model.*
import aws.sdk.kotlin.services.cloudformation.model.Stack
import aws.sdk.kotlin.services.cloudformation.waiters.waitUntilStackCreateComplete
import aws.sdk.kotlin.services.cloudformation.waiters.waitUntilStackDeleteComplete
import aws.smithy.kotlin.runtime.retries.Outcome
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object CloudformationService {



    suspend fun deployStack(region: String?, stackNameVal: String?, template: String, params: List<Parameter> = emptyList() ) {
        val request = CreateStackRequest {
            stackName = stackNameVal
            templateBody = template
            onFailure = OnFailure.Rollback
            parameters = params
        }

        CloudFormationClient { this.region = region }.use { cfClient ->
            cfClient.createStack(request)
        }
    }

    suspend fun deleteStackByName(region: String, stackNameVal: String?) {
        CloudFormationClient { this.region = region }.use { cfClient ->
            cfClient.deleteStack(DeleteStackRequest {
                stackName = stackNameVal
            })
        }
    }

    suspend fun deleteAllStackInRegion(region: String): List<String> {
        val deletedStacks = mutableListOf<String>()
        describeAllStacks(region)?.forEach { stack: Stack ->
            val stackName = stack.stackName
            deleteStackByName(region, stackName)
            stackName?.run { deletedStacks.add(stackName) }
        }
        return deletedStacks
    }

    suspend fun describeAllStacks(region: String) : List<Stack>? {
        CloudFormationClient { this.region = region }.use { cfClient ->
            val stacksResponse: DescribeStacksResponse = cfClient.describeStacks(DescribeStacksRequest {})
            return stacksResponse.stacks
        }
    }

    suspend fun describeStackResourcesByName(region: String, name: String?) : List<StackResource>? {
        CloudFormationClient { this.region = region }.use { cfClient ->
            val stacksResponse = cfClient.describeStackResources(DescribeStackResourcesRequest{stackName = name})
            return stacksResponse.stackResources
        }
    }

    suspend fun waitUntilStackDeployed(region: String, name: String?): Outcome<DescribeStacksResponse> {
        return CloudFormationClient { this.region = region }.use { cfClient ->
            cfClient.waitUntilStackCreateComplete() {
                DescribeStacksRequest {
                    stackName = name
                }
            }
        }
    }

    suspend fun waitUntilStackDeleted(region: String, name: String?): Outcome<DescribeStacksResponse> {
        return CloudFormationClient { this.region = region }.use { cfClient ->
            cfClient.waitUntilStackDeleteComplete {
                DeleteStackRequest {
                    stackName = name
                }
            }
        }
    }

    private suspend fun getStacksByName(region: String, name: String?): List<Stack>? {
        CloudFormationClient { this.region = region }.use { cfClient ->
            val stacks = cfClient.describeStacks(DescribeStacksRequest { stackName = name })
            return stacks.stacks
        }
    }

    suspend fun getStackOutputsByName(region: String, name: String?): List<Output> {
        val stacks = getStacksByName(region, name)
        return stacks?.first()?.outputs?: emptyList()
    }

}