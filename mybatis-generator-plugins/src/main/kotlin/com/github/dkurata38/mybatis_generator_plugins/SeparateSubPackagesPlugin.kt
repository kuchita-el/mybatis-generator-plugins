package com.github.dkurata38.mybatis_generator_plugins

import com.github.dkurata38.mybatis_generator_plugins.type.TargetRuntime
import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.PluginAdapter

class SeparateSubPackagesPlugin : PluginAdapter() {
	override fun validate(warnings: MutableList<String>?): Boolean {
		return true
	}

	override fun initialized(introspectedTable: IntrospectedTable) {
		val targetRuntime = TargetRuntime.valueOf(introspectedTable.context.targetRuntime)

		val translator = SubpackageTranslator.createFor(targetRuntime)
		translator.accept(introspectedTable)
	}

	sealed class SubpackageTranslator {
		abstract fun accept(introspectedTable: IntrospectedTable)

		companion object {
			private fun getLastSubpackageName(introspectedTable: IntrospectedTable): String {
				return introspectedTable.fullyQualifiedTableNameAtRuntime.toLowerCase()
			}

			private fun typeToSubpackageType(type: String, subPackageName: String): String {
				return type.replace("([^.]+)$".toRegex(), "$subPackageName.$1")
			}

			fun createFor(targetRuntime: TargetRuntime): SubpackageTranslator {
				return when(targetRuntime) {
					TargetRuntime.MyBatis3DynamicSql -> MyBatis3DynamicSqlTranslator
					TargetRuntime.MyBatis3Kotlin -> MyBatis3KotlinTranslator
					else -> MyBatis3Translator
				}
			}
		}

		object MyBatis3DynamicSqlTranslator: SubpackageTranslator() {
			override fun accept(introspectedTable: IntrospectedTable) {
				val subPackageName = getLastSubpackageName(introspectedTable)
				introspectedTable.baseRecordType = typeToSubpackageType(introspectedTable.baseRecordType, subPackageName)
				introspectedTable.myBatis3JavaMapperType = typeToSubpackageType(introspectedTable.myBatis3JavaMapperType, subPackageName)
				introspectedTable.myBatisDynamicSqlSupportType = typeToSubpackageType(introspectedTable.myBatisDynamicSqlSupportType, subPackageName)
			}
		}

		object MyBatis3KotlinTranslator: SubpackageTranslator() {
			override fun accept(introspectedTable: IntrospectedTable) {
				val subPackageName = getLastSubpackageName(introspectedTable)
				introspectedTable.kotlinRecordType = typeToSubpackageType(introspectedTable.kotlinRecordType, subPackageName)
				introspectedTable.myBatis3JavaMapperType = typeToSubpackageType(introspectedTable.myBatis3JavaMapperType, subPackageName)
				introspectedTable.myBatisDynamicSqlSupportType = typeToSubpackageType(introspectedTable.myBatisDynamicSqlSupportType, subPackageName)
			}
		}

		object MyBatis3Translator: SubpackageTranslator() {
			override fun accept(introspectedTable: IntrospectedTable) {
				val subPackageName = getLastSubpackageName(introspectedTable)
				introspectedTable.baseRecordType = typeToSubpackageType(introspectedTable.baseRecordType, subPackageName)
				introspectedTable.recordWithBLOBsType = typeToSubpackageType(introspectedTable.recordWithBLOBsType, subPackageName)
				introspectedTable.exampleType = typeToSubpackageType(introspectedTable.exampleType, subPackageName)
				introspectedTable.myBatis3JavaMapperType = typeToSubpackageType(introspectedTable.myBatis3JavaMapperType, subPackageName)
				if (introspectedTable.myBatis3XmlMapperPackage != null) {
					introspectedTable.myBatis3XmlMapperPackage = introspectedTable.myBatis3XmlMapperPackage + "." + subPackageName
				}
			}
		}
	}
}
