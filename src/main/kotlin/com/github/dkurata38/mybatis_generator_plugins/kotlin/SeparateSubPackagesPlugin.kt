package com.github.dkurata38.mybatis_generator_plugins.kotlin

import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.PluginAdapter

class SeparateSubPackagesPlugin : PluginAdapter() {
	override fun validate(warnings: MutableList<String>?): Boolean {
		return true
	}

	override fun initialized(introspectedTable: IntrospectedTable) {
		val tableName = introspectedTable.fullyQualifiedTable.fullyQualifiedTableNameAtRuntime
		// MyBatis3
		if (introspectedTable.baseRecordType != null) {
			introspectedTable.baseRecordType = typeToSubpackageType(introspectedTable.baseRecordType, tableName)
		}
		if (introspectedTable.recordWithBLOBsType != null) {
			introspectedTable.recordWithBLOBsType = typeToSubpackageType(introspectedTable.recordWithBLOBsType, tableName)
		}
		if (introspectedTable.myBatis3JavaMapperType != null) {
			introspectedTable.myBatis3JavaMapperType = typeToSubpackageType(introspectedTable.myBatis3JavaMapperType, tableName)
		}
		if (introspectedTable.exampleType != null) {
			introspectedTable.exampleType = typeToSubpackageType(introspectedTable.exampleType, tableName)
		}
		// SQLMAPPER/MIXEDMAPPER
		if (introspectedTable.myBatis3XmlMapperPackage != null) {
			introspectedTable.myBatis3XmlMapperPackage = introspectedTable.myBatis3XmlMapperPackage + "." + tableName
		}
		// ANNOTATEDMAPPER
		if (introspectedTable.myBatis3SqlProviderType != null) {
			introspectedTable.myBatis3SqlProviderType = typeToSubpackageType(introspectedTable.myBatis3SqlProviderType, tableName)
		}

		// MyBatisDynamicSQL
		if (introspectedTable.myBatisDynamicSqlSupportType != null) {
			introspectedTable.myBatisDynamicSqlSupportType = typeToSubpackageType(introspectedTable.myBatisDynamicSqlSupportType, tableName)
		}

		//MyBatis3Kotlin
		if (introspectedTable.kotlinRecordType != null) {
			introspectedTable.kotlinRecordType = typeToSubpackageType(introspectedTable.kotlinRecordType, tableName)
		}
	}

	private fun typeToSubpackageType(type: String, subPackageName: String): String {
		return type.replace("([^.]+)$".toRegex(), "$subPackageName.$1")
	}

}
