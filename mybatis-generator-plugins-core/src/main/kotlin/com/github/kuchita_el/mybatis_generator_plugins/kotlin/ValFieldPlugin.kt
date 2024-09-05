package com.github.kuchita_el.mybatis_generator_plugins.kotlin

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
                if (it.type == KotlinProperty.Type.VAL) it else toValProperty(it)
            }
        return super.kotlinDataClassGenerated(kotlinFile, dataClass, introspectedTable)
    }

    companion object {
        fun toValProperty(property: KotlinProperty): KotlinProperty {
            var builder = KotlinProperty.newVal(property.name)
            property.dataType.ifPresent { dataType -> builder = builder.withDataType(dataType) }
            property.modifiers.forEach { modifier -> builder = builder.withModifier(modifier) }
            property.annotations.forEach { annotation -> builder = builder.withAnnotation(annotation) }
            property.initializationString.ifPresent { initializationString ->
                builder = builder.withInitializationString(initializationString)
            }
            return builder.build()
        }
    }
}
