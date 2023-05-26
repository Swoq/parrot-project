package com.swoqe.parrot.configuration.dto

data class Aws(var region: String?, var ec2ImageId: String?, var services: List<Service>)
