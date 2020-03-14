package com.github.dkurata38.mybatis_generator_plugins.xml_mapper

import org.mybatis.generator.api.FullyQualifiedTable
import org.mybatis.generator.api.IntrospectedColumn
import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.PluginAdapter
import org.mybatis.generator.api.dom.xml.Attribute
import org.mybatis.generator.api.dom.xml.Document
import org.mybatis.generator.api.dom.xml.TextElement
import org.mybatis.generator.api.dom.xml.XmlElement
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities

class CustomAliasedColumnListPlugin : PluginAdapter() {

	private val elementsToAdd = mutableMapOf<FullyQualifiedTable, List<XmlElement>>()

	override fun validate(warnings: MutableList<String>?): Boolean {
		return true
	}

	override fun sqlMapBaseColumnListElementGenerated(element: XmlElement, introspectedTable: IntrospectedTable): Boolean {
		val customAliasedBaseColumnListElement = XmlElement("sql")
		customAliasedBaseColumnListElement.addAttribute(Attribute("id", "Custom_Aliased_" + introspectedTable.baseColumnListId))
		element.elements
				.filterIsInstance<TextElement>()
				.map {
					val oldContent = it.content
					val newContent = introspectedTable
							.nonBLOBColumns
							.fold(oldContent, { acc, introspectedColumn -> selectPhraseToCustomAliasedSelectPhrase(acc, introspectedColumn) })
					TextElement(newContent)
				}
				.forEach { customAliasedBaseColumnListElement.addElement(it) }

		elementsToAdd[introspectedTable.fullyQualifiedTable] = (elementsToAdd[introspectedTable.fullyQualifiedTable]
				?: emptyList()).plus(customAliasedBaseColumnListElement)
		return true
	}

	override fun sqlMapBlobColumnListElementGenerated(element: XmlElement, introspectedTable: IntrospectedTable): Boolean {
		val customAliasedBaseColumnListElement = XmlElement("sql")
		customAliasedBaseColumnListElement.addAttribute(Attribute("id", "Custom_Aliased_" + introspectedTable.blobColumnListId))
		element.elements
				.filterIsInstance<TextElement>()
				.map {
					val oldContent = it.content
					val newContent = introspectedTable
							.blobColumns
							.fold(oldContent, { acc, introspectedColumn -> selectPhraseToCustomAliasedSelectPhrase(acc, introspectedColumn) })
					TextElement(newContent)
				}
				.forEach { customAliasedBaseColumnListElement.addElement(it) }

		elementsToAdd[introspectedTable.fullyQualifiedTable] = (elementsToAdd[introspectedTable.fullyQualifiedTable]
				?: emptyList()).plus(customAliasedBaseColumnListElement)
		return true
	}

	override fun sqlMapDocumentGenerated(document: Document, introspectedTable: IntrospectedTable): Boolean {
		val rootElement = document.rootElement
		val elements = elementsToAdd[introspectedTable.fullyQualifiedTable]
		elements?.forEach { rootElement.addElement(it) }

		return true
	}

	private fun selectPhraseToCustomAliasedSelectPhrase(content: String, introspectedColumn: IntrospectedColumn): String {
		val builder = StringBuilder()
		builder.append("\${alias}")
		builder.append(".")
		builder.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn))
		builder.append(" as ")
		if (introspectedColumn.isColumnNameDelimited) {
			builder.append(introspectedColumn.context.beginningDelimiter)
		}
		builder.append("\${alias}")
		builder.append("_")
		builder.append(introspectedColumn.actualColumnName)
		if (introspectedColumn.isColumnNameDelimited) {
			builder.append(introspectedColumn.context.endingDelimiter)
		}

		val newContent = builder.toString()
		return content.replace(MyBatis3FormattingUtilities.getSelectListPhrase(introspectedColumn), newContent)
	}
}
