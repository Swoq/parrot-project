package com.swoqe.parrot.aws

data class Ec2Docker(
    val docker_image: String,
    val inner_port: String,
    val ec2_image_id: String,
    val db_endpoint: String,
    val db_user: String,
    val db_password: String,
)
