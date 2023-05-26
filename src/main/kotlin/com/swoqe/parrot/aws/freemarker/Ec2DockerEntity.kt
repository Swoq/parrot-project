package com.swoqe.parrot.aws.freemarker

data class Ec2DockerEntity(
    val docker_image: String,
    val inner_port: String,
    val envs: String
)
