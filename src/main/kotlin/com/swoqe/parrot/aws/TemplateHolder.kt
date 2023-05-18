package com.swoqe.parrot.aws

import java.io.FileNotFoundException

object TemplateHolder {

    fun ec2Docker(
        dockerImage: String, containerPort: String, ec2ImageId: String,
        dbEndPoint: String, dbUser: String, dbPassword: String
    ): String {
        val template = javaClass.getResource("cf-templates/ec2.yaml")
            ?.readText(Charsets.UTF_8)
            ?: throw FileNotFoundException()

        return template.replace("\${inner_port}", containerPort)
            .replace("\${docker_image}", dockerImage)
            .replace("\${db_endpoint}", dbEndPoint)
            .replace("\${db_user}", dbUser)
            .replace("\${db_password}", dbPassword)
            .replace("\${ec2_image_id}", ec2ImageId)
    }
}