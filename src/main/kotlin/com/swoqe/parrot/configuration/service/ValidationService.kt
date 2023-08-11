package com.swoqe.parrot.configuration.service

import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.exceptions.ProcessingException
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.swoqe.parrot.configuration.util.JacksonYamlMapper
import java.io.File
import java.io.IOException
import java.nio.file.Paths

object ValidationService {
    fun validate (yamlFile: File, jsonSchema: String): ProcessingReport? {
        try {
            // Load the YAML file
            val yamlNode = JacksonYamlMapper.instance.readTree(yamlFile)

            // Load the JSON schema file
            val jsonSchemaNode = JsonLoader.fromString(jsonSchema)

            // Create the JSON schema factory
            val schemaFactory = JsonSchemaFactory.byDefault()

            return schemaFactory.getJsonSchema(jsonSchemaNode).validate(yamlNode)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ProcessingException) {
            e.printStackTrace()
        }
        return null
    }

}
