package com.github.kuchita_el.mybatis_generator_plugins.generators

import org.mybatis.generator.api.dom.OutputUtilities.javaIndent
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType
import org.mybatis.generator.api.dom.java.Interface
import org.mybatis.generator.api.dom.java.JavaVisibility
import org.mybatis.generator.api.dom.java.Method
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.annotated.AnnotatedSelectByPrimaryKeyMethodGenerator
import org.mybatis.generator.internal.util.StringUtility.escapeStringForJava

internal class AnnotatedSelectByPrimaryKeyForUpdateMethodGenerator(
    private val useResultMapIfAvailable: Boolean,
    private val isSimple: Boolean,
    private val methodName: String,
) : AnnotatedSelectByPrimaryKeyMethodGenerator(useResultMapIfAvailable, isSimple) {
    override fun addInterfaceElements(interfaze: Interface) {
        val method = Method(methodName)
        method.visibility = JavaVisibility.PUBLIC
        method.isAbstract = true

        val returnType = introspectedTable.rules.calculateAllFieldsClass()
        method.setReturnType(returnType)

        val importedTypes = mutableSetOf(returnType)

        addPrimaryKeyMethodParameters(isSimple, method, importedTypes)
        addMapperAnnotations(interfaze, method)

        context.commentGenerator.addGeneralMethodComment(method, introspectedTable)

        interfaze.addImportedTypes(importedTypes)
        interfaze.addMethod(method)
    }

    override fun addMapperAnnotations(
        interfaze: Interface,
        method: Method,
    ) {
        interfaze.addImportedType(FullyQualifiedJavaType("org.apache.ibatis.annotations.Select"))
        val selectAnnotation = buildSelectForUpdateAnnotation()
        selectAnnotation.forEach { method.annotations.add(it) }

        if (useResultMapIfAvailable) {
            if (introspectedTable.rules.generateBaseResultMap() ||
                introspectedTable.rules.generateResultMapWithBLOBs()
            ) {
                addResultMapAnnotation(method)
            } else {
                addAnnotatedResults(interfaze, method, introspectedTable.nonPrimaryKeyColumns)
            }
        } else {
            addAnnotatedResults(interfaze, method, introspectedTable.nonPrimaryKeyColumns)
        }
    }

    private fun addResultMapAnnotation(method: Method) {
        val annotation =
            "@ResultMap(\"%s.%s\")"
                .format(
                    introspectedTable.myBatis3SqlMapNamespace,
                    if (introspectedTable.rules.generateResultMapWithBLOBs()) {
                        introspectedTable.resultMapWithBLOBsId
                    } else {
                        introspectedTable.baseResultMapId
                    },
                )
        method.addAnnotation(annotation)
    }

    private fun buildSelectForUpdateAnnotation(): List<String> {
        val initial = buildInitialSelectAnnotationStrings()
        val sb = StringBuilder()
        javaIndent(sb, 1)
        sb.append("\"from ")
        sb.append(escapeStringForJava(introspectedTable.fullyQualifiedTableNameAtRuntime))
        sb.append("\",")
        val fromClause = sb.toString()
        val whereClause = buildByPrimaryKeyWhereClause() + ","
        sb.clear()
        javaIndent(sb, 1)
        sb.append("\"for update\"")
        val forUpdateClause = sb.toString()
        val endOfAnnotation = "})"

        return initial + fromClause + whereClause + forUpdateClause +
            endOfAnnotation
    }
}
