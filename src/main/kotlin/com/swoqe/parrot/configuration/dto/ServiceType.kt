package com.swoqe.parrot.configuration.dto

import com.fasterxml.jackson.annotation.JsonValue

enum class ServiceType(private val value: String) {

    ALB("AWS::ElasticLoadBalancingV2"),
    VPC("AWS::EC2::VPC"),
    SG("AWS::AutoScaling::AutoScalingGroup");

    @JsonValue
    fun getValue(): String {
        return value
    }
}