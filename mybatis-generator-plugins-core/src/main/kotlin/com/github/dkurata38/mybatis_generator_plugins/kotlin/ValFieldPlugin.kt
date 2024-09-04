package com.github.dkurata38.mybatis_generator_plugins.kotlin

import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.PluginAdapter
import org.mybatis.generator.api.dom.kotlin.KotlinFile
import org.mybatis.generator.api.dom.kotlin.KotlinProperty
import org.mybatis.generator.api.dom.kotlin.KotlinType

class ValFieldPlugin : PluginAdapter() {
    override fun validate(warnings: MutableList<String>?): Boolean {
        return true
    }

    override fun kotlinDataClassGenerated(
        kotlinFile: KotlinFile,
        dataClass: KotlinType,
        introspectedTable: IntrospectedTable
    ): Boolean {
        dataClass.constructorProperties
            .replaceAll {
                var builder = KotlinProperty.newVal(it.name)
                it.dataType.ifPresent { dataType -> builder = builder.withDataType(dataType) }
                it.modifiers.forEach { modifier -> builder = builder.withModifier(modifier) }
                it.annotations.forEach { annotation -> builder = builder.withAnnotation(annotation) }
                it.initializationString.ifPresent { initializationString ->
                    builder = builder.withInitializationString(initializationString)
                }
                builder.build()
            }
        return super.kotlinDataClassGenerated(kotlinFile, dataClass, introspectedTable)
    }
}
