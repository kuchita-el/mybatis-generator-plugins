package com.github.dkurata38.mybatis_generator_plugins.kotlin

import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.PluginAdapter
import org.mybatis.generator.api.dom.kotlin.KotlinFile
import org.mybatis.generator.api.dom.kotlin.KotlinModifier
import org.mybatis.generator.api.dom.kotlin.KotlinProperty
import org.mybatis.generator.api.dom.kotlin.KotlinType

class NotNullFieldPlugin: PluginAdapter() {
    override fun validate(warnings: MutableList<String>?): Boolean {
        return true
    }

    override fun kotlinDataClassGenerated(kotlinFile: KotlinFile?, dataClass: KotlinType?, introspectedTable: IntrospectedTable?): Boolean {
        if (kotlinFile == null || dataClass == null || introspectedTable == null) {
            return super.kotlinDataClassGenerated(kotlinFile, dataClass, introspectedTable)
        }

        if (introspectedTable.context.targetRuntime != "MyBatis3Kotlin") {
            return super.kotlinDataClassGenerated(kotlinFile, dataClass, introspectedTable)
        }

        val properties = dataClass.constructorProperties
                .filterNotNull()
                .filterNot { isNullableProperty(it, introspectedTable) }
                .map { property ->
                    val dataType =
                            property.dataType.orElse("").removeSuffix("?")
                    property.initializationString
                            .map { generateKotlinProperty(property.name, dataType, property.modifiers, property.type, it) }
                            .orElseGet { generateKotlinProperty(property.name, dataType, property.modifiers, property.type) }
                }
        dataClass.constructorProperties.replaceAll {
            val property = properties
                    .find { kotlinProperty -> it.name == kotlinProperty.name }
            property?: it
        }
        return super.kotlinDataClassGenerated(kotlinFile, dataClass, introspectedTable)
    }

    fun isNullableProperty(kotlinProperty: KotlinProperty, introspectedTable: IntrospectedTable): Boolean {
        return introspectedTable
                .allColumns
                .find { kotlinProperty.name == it.javaProperty }
                ?.isNullable!!
    }

    fun generateKotlinProperty(propertyName: String, dataType: String, modifiers: List<KotlinModifier>, type: KotlinProperty.Type, initializationString: String): KotlinProperty {
        var builder = if (type == KotlinProperty.Type.VAL) KotlinProperty.newVal(propertyName) else KotlinProperty.newVar(propertyName)
        builder = builder
                .withDataType(dataType)
                .withInitializationString(initializationString)
        modifiers
                .forEach{ builder = builder.withModifier(it) }
        return builder.build()
    }

    fun generateKotlinProperty(propertyName: String, dataType: String, modifiers: List<KotlinModifier>, type: KotlinProperty.Type): KotlinProperty {
        var builder = if (type == KotlinProperty.Type.VAL) KotlinProperty.newVal(propertyName) else KotlinProperty.newVar(propertyName)
        builder = builder
                .withDataType(dataType)
        modifiers
                .forEach{ builder = builder.withModifier(it) }
        return builder.build()
    }
}
