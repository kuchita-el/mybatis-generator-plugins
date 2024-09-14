package com.github.kuchita_el.mybatis_generator_plugins.kotlin

import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.PluginAdapter
import org.mybatis.generator.api.dom.kotlin.KotlinFile
import org.mybatis.generator.api.dom.kotlin.KotlinProperty
import org.mybatis.generator.api.dom.kotlin.KotlinType

class NotNullFieldPlugin : PluginAdapter() {
    override fun validate(warnings: MutableList<String>?): Boolean = true

    override fun kotlinDataClassGenerated(
        kotlinFile: KotlinFile?,
        dataClass: KotlinType?,
        introspectedTable: IntrospectedTable?,
    ): Boolean {
        if (this.context.targetRuntime != "MyBatis3Kotlin") {
            return super.kotlinDataClassGenerated(kotlinFile, dataClass, introspectedTable)
        }

        if (kotlinFile == null || dataClass == null || introspectedTable == null) {
            return super.kotlinDataClassGenerated(kotlinFile, dataClass, introspectedTable)
        }

        val notNullColumnNames = findNotNullPropertyNames(introspectedTable)

        dataClass.constructorProperties.replaceAll {
            if (notNullColumnNames.contains(it.name)) toNonnullProperty(it) else it
        }
        return super.kotlinDataClassGenerated(kotlinFile, dataClass, introspectedTable)
    }

    companion object {
        /**
         * notnull制約がかけられたカラムに対応するプロパティ名を検索する
         */
        fun findNotNullPropertyNames(introspectedTable: IntrospectedTable): Set<String> =
            introspectedTable
                .allColumns
                .filterNot { it.isNullable }
                .map { it.javaProperty }
                .toSet()

        /**
         * デフォルトで生成されるKotlinDataClassのコンストラクタパラメータをもとに, 非null型のプロパティを作成する。
         * 引数省略時のデフォルト値がnullに設定されていた場合、コンパイルエラーを防ぐためにデフォルト値を削除する。
         */
        fun toNonnullProperty(kotlinProperty: KotlinProperty): KotlinProperty {
            var builder =
                if (kotlinProperty.type == KotlinProperty.Type.VAL) {
                    KotlinProperty.newVal(kotlinProperty.name)
                } else {
                    KotlinProperty.newVar(kotlinProperty.name)
                }

            val dataType = kotlinProperty.dataType.orElse("").removeSuffix("?")
            builder = builder.withDataType(dataType)
            kotlinProperty.modifiers
                .forEach { builder = builder.withModifier(it) }
            kotlinProperty.initializationString
                .filter { it.equals("null").not() }
                .ifPresent { builder = builder.withInitializationString(it) }
            kotlinProperty.annotations.forEach { builder = builder.withAnnotation(it) }
            return builder.build()
        }
    }
}
