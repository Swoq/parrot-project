package com.swoqe.parrot.configuration.dto

data class Service(
    var type: ServiceType,
    var properties: Map<String, String>?
)
