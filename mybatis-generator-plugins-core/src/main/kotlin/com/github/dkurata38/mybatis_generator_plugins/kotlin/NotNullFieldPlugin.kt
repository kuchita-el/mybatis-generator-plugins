package com.github.dkurata38.mybatis_generator_plugins.kotlin

import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.PluginAdapter
import org.mybatis.generator.api.dom.kotlin.KotlinFile
import org.mybatis.generator.api.dom.kotlin.KotlinProperty
import org.mybatis.generator.api.dom.kotlin.KotlinType

class NotNullFieldPlugin : PluginAdapter() {
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
				.map { it.toImmutableProperty() }

		dataClass.constructorProperties.replaceAll {
			val property = properties
					.find { kotlinProperty -> it.name == kotlinProperty.name }
			property ?: it
		}
		return super.kotlinDataClassGenerated(kotlinFile, dataClass, introspectedTable)
	}

	/**
	 * 第一引数で指定したKotlinDataClassのコンストラクタパラメータがNotNullカラムに対応したものかどうかを判定する。
	 */
	fun isNullableProperty(kotlinProperty: KotlinProperty, introspectedTable: IntrospectedTable): Boolean {
		return introspectedTable
				.allColumns
				.find { kotlinProperty.name == it.javaProperty }
				?.isNullable!!
	}

	/**
	 * デフォルトで生成されるKotlinDataClassのコンストラクタパラメータをもとに
	 * イミュータブルなコンストラクタパラメータを生成する。
	 * 再代入不可能な型はNonNull型でパラメータ名はデフォルトで生成されるものと同様。
	 */
	fun KotlinProperty.toImmutableProperty(): KotlinProperty {
		val dataType = this.dataType.orElse("").removeSuffix("?")
		var builder = KotlinProperty.newVal(this.name)
				.withDataType(dataType)
		this.modifiers
				.forEach { builder = builder.withModifier(it) }
		return builder.build()
	}
}
