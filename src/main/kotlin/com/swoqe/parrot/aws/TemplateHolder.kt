package com.swoqe.parrot.aws

import freemarker.cache.NullCacheStorage
import freemarker.template.Configuration
import java.io.StringWriter

object TemplateHolder {

    private val freeMarker = Configuration(Configuration.VERSION_2_3_32).apply {
        defaultEncoding = Charsets.UTF_8.name()
        cacheStorage = NullCacheStorage.INSTANCE
        setClassForTemplateLoading(javaClass, "/cf-templates/")
    }

    fun ec2Docker(entity: Ec2Docker): String =
        StringWriter().apply { freeMarker.getTemplate("ec2.yaml").process(entity, this) }.toString()

}
