package com.github.kuchita_el.mybatis_generator_plugins

import org.mybatis.generator.api.FullyQualifiedTable
import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.PluginAdapter
import org.mybatis.generator.api.dom.java.Interface
import org.mybatis.generator.api.dom.java.Method
import org.mybatis.generator.api.dom.xml.Attribute
import org.mybatis.generator.api.dom.xml.Document
import org.mybatis.generator.api.dom.xml.TextElement
import org.mybatis.generator.api.dom.xml.XmlElement

class SelectForUpdatePlugin : PluginAdapter() {
    companion object {
        const val METHOD_SUFFIX = "ForUpdate"
    }

    private val elementsToAdd: MutableMap<FullyQualifiedTable, MutableList<XmlElement>> = mutableMapOf()

    enum class JavaClientGeneratorConfigurationType(val value: String) {
        XmlMapper("XMLMAPPER"), MixedMapper("MIXEDMAPPER"), AnnotatedMapper("ANNOTATEDMAPPER"),

    }

    override fun validate(warnings: MutableList<String>): Boolean {
        return true
    }

    override fun clientSelectByPrimaryKeyMethodGenerated(
        method: Method,
        interfaze: Interface,
        introspectedTable: IntrospectedTable
    ): Boolean {
        when (context.javaClientGeneratorConfiguration.configurationType) {
            JavaClientGeneratorConfigurationType.XmlMapper.value -> {
                val selectByPrimaryKeyForUpdate = Method(method)
                selectByPrimaryKeyForUpdate.name += METHOD_SUFFIX
                interfaze.addMethod(selectByPrimaryKeyForUpdate)
            }
            else -> {}
        }
        return super.clientSelectByPrimaryKeyMethodGenerated(method, interfaze, introspectedTable)
    }

    override fun sqlMapSelectByPrimaryKeyElementGenerated(
        element: XmlElement,
        introspectedTable: IntrospectedTable
    ): Boolean {
        val selectByPrimaryKeyForUpdate = XmlElement(element)
        selectByPrimaryKeyForUpdate.attributes.replaceAll {
            if (it.name == "id") Attribute(
                it.name,
                it.value + METHOD_SUFFIX
            ) else it
        }
        selectByPrimaryKeyForUpdate.addElement(TextElement("for update"))

        storeElementToAdd(selectByPrimaryKeyForUpdate, introspectedTable)

        return super.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable)
    }

    override fun sqlMapDocumentGenerated(document: Document, introspectedTable: IntrospectedTable): Boolean {
        val elements = elementsToAdd.getOrElse(introspectedTable.fullyQualifiedTable) { mutableListOf() }
        elements.forEach { document.rootElement.addElement(it) }
        return super.sqlMapDocumentGenerated(document, introspectedTable)
    }

    private fun storeElementToAdd(elementToAdd: XmlElement, introspectedTable: IntrospectedTable) {
        if (!elementsToAdd.containsKey(introspectedTable.fullyQualifiedTable)) {
            elementsToAdd[introspectedTable.fullyQualifiedTable] = mutableListOf()
        }
        elementsToAdd.getValue(introspectedTable.fullyQualifiedTable).add(elementToAdd)
    }

}

