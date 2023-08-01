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
		fun findNotNullPropertyNames(introspectedTable: IntrospectedTable): Set<String> {
			return introspectedTable
					.allColumns
					.filterNot { it.isNullable }
					.map { it.javaProperty }
					.toSet()
		}


		/**
		 * デフォルトで生成されるKotlinDataClassのコンストラクタパラメータをもとに
		 * イミュータブルなコンストラクタパラメータを生成する。
		 * 再代入不可能な型はNonNull型でパラメータ名はデフォルトで生成されるものと同様。
		 */
		fun toNonnullProperty(kotlinProperty: KotlinProperty): KotlinProperty {
			val dataType = kotlinProperty.dataType.orElse("").removeSuffix("?")
			var builder = KotlinProperty.newVal(kotlinProperty.name)
					.withDataType(dataType)
			kotlinProperty.modifiers
					.forEach { builder = builder.withModifier(it) }
			return builder.build()
		}
	}
}
