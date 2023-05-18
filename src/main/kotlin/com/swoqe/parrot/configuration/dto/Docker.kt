package com.swoqe.parrot.configuration.dto

data class Docker(
    var image: String,
    var innerPort: String,
    var dbEndpoint: String,
    var dbUser: String,
    var dbPassword: String
)
