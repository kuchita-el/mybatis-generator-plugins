package com.github.kuchita_el.mybatis_generator_plugins

import com.github.kuchita_el.mybatis_generator_plugins.generators.AnnotatedSelectByPrimaryKeyForUpdateMethodGenerator
import org.mybatis.generator.api.FullyQualifiedTable
import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.PluginAdapter
import org.mybatis.generator.api.dom.java.Interface
import org.mybatis.generator.api.dom.java.Method
import org.mybatis.generator.api.dom.xml.Attribute
import org.mybatis.generator.api.dom.xml.Document
import org.mybatis.generator.api.dom.xml.TextElement
import org.mybatis.generator.api.dom.xml.XmlElement
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator
import org.mybatis.generator.config.Context

class SelectForUpdatePlugin : PluginAdapter() {
    companion object {
        const val METHOD_SUFFIX = "ForUpdate"
    }

    private val elementsToAdd: MutableMap<FullyQualifiedTable, MutableList<XmlElement>> = mutableMapOf()

    enum class JavaClientGeneratorConfigurationType(val value: String) {
        XmlMapper("XMLMAPPER"), MixedMapper("MIXEDMAPPER"), AnnotatedMapper("ANNOTATEDMAPPER"),

    }

    enum class TargetRuntime {
        MyBatis3Kotlin, MyBatis3, MyBatis3DynamicSql, MyBatis3Simple
    }

    override fun validate(warnings: MutableList<String>): Boolean {
        return true
    }

    override fun clientSelectByPrimaryKeyMethodGenerated(
        method: Method,
        interfaze: Interface,
        introspectedTable: IntrospectedTable
    ): Boolean {
        when (context.targetRuntime) {
            TargetRuntime.MyBatis3Simple.name -> {
                when (context.javaClientGeneratorConfiguration.configurationType) {
                    JavaClientGeneratorConfigurationType.XmlMapper.value -> {
                        val selectByPrimaryKeyForUpdate = Method(method)
                        selectByPrimaryKeyForUpdate.name += METHOD_SUFFIX
                        interfaze.addMethod(selectByPrimaryKeyForUpdate)
                    }

                    JavaClientGeneratorConfigurationType.AnnotatedMapper.value -> {
                        executeJavaMapperMethodGenerator(
                            {
                                AnnotatedSelectByPrimaryKeyForUpdateMethodGenerator(
                                    useResultMapIfAvailable = false,
                                    isSimple = true,
                                    methodName = method.name + METHOD_SUFFIX
                                )
                            },
                            context,
                            introspectedTable,
                            interfaze
                        )
                    }
                }
            }

            TargetRuntime.MyBatis3.name -> {
                when (context.javaClientGeneratorConfiguration.configurationType) {
                    JavaClientGeneratorConfigurationType.XmlMapper.value -> {
                        val selectByPrimaryKeyForUpdate = Method(method)
                        selectByPrimaryKeyForUpdate.name += METHOD_SUFFIX
                        interfaze.addMethod(selectByPrimaryKeyForUpdate)
                    }

                    JavaClientGeneratorConfigurationType.MixedMapper.value -> {
                        executeJavaMapperMethodGenerator(
                            {AnnotatedSelectByPrimaryKeyForUpdateMethodGenerator(
                                useResultMapIfAvailable = true,
                                isSimple = false,
                                methodName = method.name + METHOD_SUFFIX
                            )},
                            context, introspectedTable, interfaze
                        )
                    }

                    JavaClientGeneratorConfigurationType.AnnotatedMapper.value -> {
                        executeJavaMapperMethodGenerator(
                            {AnnotatedSelectByPrimaryKeyForUpdateMethodGenerator(
                                useResultMapIfAvailable = false,
                                isSimple = false,
                                methodName = method.name + METHOD_SUFFIX
                            )},
                            context, introspectedTable, interfaze
                        )
                    }

                    else -> {}
                }
            }
        }

        return super.clientSelectByPrimaryKeyMethodGenerated(method, interfaze, introspectedTable)
    }

    private fun executeJavaMapperMethodGenerator(
        generatorProvider: () -> AbstractJavaMapperMethodGenerator,
        context: Context,
        introspectedTable: IntrospectedTable,
        interfaze: Interface
    ) {
        val generator = generatorProvider()
        generator.setContext(context)
        generator.setIntrospectedTable(introspectedTable)
        generator.addInterfaceElements(interfaze)
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

