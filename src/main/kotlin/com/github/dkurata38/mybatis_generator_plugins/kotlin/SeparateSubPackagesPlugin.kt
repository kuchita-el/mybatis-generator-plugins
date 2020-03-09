package com.github.dkurata38.mybatis_generator_plugins.kotlin

import org.mybatis.generator.api.FullyQualifiedTable
import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.PluginAdapter
import org.mybatis.generator.config.PropertyRegistry
import org.mybatis.generator.internal.util.StringUtility.isTrue
import org.mybatis.generator.internal.util.StringUtility.stringContainsSpace

class SeparateSubPackagesPlugin : PluginAdapter() {
	override fun validate(warnings: MutableList<String>?): Boolean {
		return true
	}

	override fun initialized(introspectedTable: IntrospectedTable) {
		val tableName = introspectedTable.fullyQualifiedTable.fullyQualifiedTableNameAtRuntime
//		val fullyQualifiedTable = introspectedTable.fullyQualifiedTable
//		val originalDomainObjectName = fullyQualifiedTable.domainObjectName
//		val newDomainObjectName = originalDomainObjectName.plus(".$tableName")
//		val tableConfiguration = introspectedTable.tableConfiguration
//		val delimitIdentifiers = tableConfiguration.isDelimitIdentifiers
//				|| stringContainsSpace(tableConfiguration.catalog)
//				|| stringContainsSpace(tableConfiguration.schema)
//				|| stringContainsSpace(tableConfiguration.tableName)
//		introspectedTable.fullyQualifiedTable =
//				FullyQualifiedTable(
//						fullyQualifiedTable.introspectedCatalog,
//						fullyQualifiedTable.introspectedSchema,
//						fullyQualifiedTable.introspectedTableName,
//						newDomainObjectName,
//						fullyQualifiedTable.alias,
//						isTrue(tableConfiguration.getProperty(PropertyRegistry.TABLE_IGNORE_QUALIFIERS_AT_RUNTIME)),
//						tableConfiguration.getProperty(PropertyRegistry.TABLE_RUNTIME_CATALOG),
//						tableConfiguration.getProperty(PropertyRegistry.TABLE_RUNTIME_SCHEMA),
//						tableConfiguration.getProperty(PropertyRegistry.TABLE_RUNTIME_TABLE_NAME),
//						delimitIdentifiers,
//						null,
//						context
//				)
//		if (introspectedTable.context.javaModelGeneratorConfiguration != null) {
//			introspectedTable.context.javaModelGeneratorConfiguration.targetPackage = typeToSubpackageType(introspectedTable.context.javaModelGeneratorConfiguration.targetPackage, tableName)
//		}
//		if (introspectedTable.context.javaClientGeneratorConfiguration != null) {
//			introspectedTable.context.javaClientGeneratorConfiguration.targetPackage = typeToSubpackageType(introspectedTable.context.javaClientGeneratorConfiguration.targetPackage, tableName)
//		}
//		if (introspectedTable.context.sqlMapGeneratorConfiguration != null) {
//			introspectedTable.context.sqlMapGeneratorConfiguration.targetPackage = typeToSubpackageType(introspectedTable.context.sqlMapGeneratorConfiguration.targetPackage, tableName)
//		}
		// MyBatis3
		if (introspectedTable.baseRecordType != null) {
			introspectedTable.baseRecordType = typeToSubpackageType(introspectedTable.baseRecordType, tableName)
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

//		introspectedTable.myBatisDynamicSqlSupportType = typeToSubpackageType(introspectedTable.myBatisDynamicSqlSupportType, tableName)
//		super.initialized(introspectedTable)
	}

	private fun typeToSubpackageType(type: String, subPackageName: String): String {
		return type.replace("([^.]+)$".toRegex(), "$subPackageName.$1")
	}

}
