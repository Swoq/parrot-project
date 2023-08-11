package com.swoqe.parrot.aws

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.util.JSONWrappedObject
import com.swoqe.parrot.aws.TemplateHolder.substituteProperties
import com.swoqe.parrot.aws.freemarker.Ec2DockerEntity
import com.swoqe.parrot.configuration.util.JacksonYamlMapper
import freemarker.cache.NullCacheStorage
import freemarker.template.Configuration
import java.io.StringWriter

object TemplateHolder {

    private val freeMarker = Configuration(Configuration.VERSION_2_3_32).apply {
        defaultEncoding = Charsets.UTF_8.name()
        cacheStorage = NullCacheStorage.INSTANCE
        setClassForTemplateLoading(javaClass, "/cf-templates/")
    }

    fun ec2Docker(entity: Ec2DockerEntity): String {
        return StringWriter().apply { freeMarker.getTemplate("ec2.yaml").process(entity, this) }.toString()
    }

    fun ec2Vpc(entity: Ec2DockerEntity): String {
        return StringWriter().apply { freeMarker.getTemplate("ec2-vpc.yaml").process(entity, this) }.toString()
    }

    fun albSgVpc(entity: Ec2DockerEntity, props: Map<String, String>): String {
        val template = StringWriter().apply { freeMarker.getTemplate("alb-sg.yaml").process(entity, this) }.toString()
        return substituteProperties(template, props)
    }

    fun substituteProperties(template: String, props: Map<String, String>) : String {
        val newTemplate = escapeAwsFunctions(template)
        val tree = JacksonYamlMapper.instance.readTree(newTemplate)
        props.entries.forEach { entry ->
            val parent = tree.findParent(entry.key)
            if (parent is ObjectNode) {
                parent.put(entry.key, entry.value)
            }
        }
        return unescapeAwsFunctions(JacksonYamlMapper.instance.writeValueAsString(tree))
    }

    private fun escapeAwsFunctions(template: String): String {
        return template.replace("!Ref", "Ref")
            .replace("!GetAtt", "GetAtt")
            .replace("!Select [0, !GetAZs '']", "Fn::Select1")
            .replace("!Select [1, !GetAZs '']", "Fn::Select2")

    }

    private fun unescapeAwsFunctions(template: String) : String {
        return template.replace("Ref", "!Ref")
            .replace("GetAtt", "!GetAtt")
            .replace("Fn::Select1", "!Select [0, !GetAZs '']")
            .replace("Fn::Select2", "!Select [1, !GetAZs '']")
    }
}