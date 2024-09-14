package com.github.kuchita_el.mybatis_generator_plugins.kotlin

import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mybatis.generator.api.IntrospectedColumn
import org.mybatis.generator.api.dom.kotlin.KotlinModifier
import org.mybatis.generator.api.dom.kotlin.KotlinProperty
import org.mybatis.generator.runtime.kotlin.IntrospectedTableKotlinImpl
import java.util.Optional

class NotNullFieldPluginTest {
    @Nested
    inner class ToNonnullPropertyTest {
        @ParameterizedTest
        @CsvSource(
            "VAL, field1, String?, PRIVATE, @Getter, defaultString, String, defaultString", // VAL、 元がNullable、 デフォルト値がNULLではない
            "VAR, field2, Int, PRIVATE, @Setter, 0, Int, 0", // VAR、元がNotNull、デフォルト値がNULL以外
            "VAL, field3, String?, PRIVATE, @Getter, null, String, NULL", // VAL、元がNullable、デフォルト値がnull
            "VAL, field4, String, NULL, NULL, NULL, String, NULL", // VAL、元がNotNull、オプションのパラメータが全部Null
            nullValues = ["NULL"],
        )
        fun プロパティのデータ型がNotNull型になる(
            type: KotlinProperty.Type,
            name: String,
            dataType: String,
            modifier: KotlinModifier?,
            annotation: String?,
            initializationString: String?,
            expectedDataType: String,
            expectedInitializationString: String?,
        ) {
            var builder =
                if (type == KotlinProperty.Type.VAL) KotlinProperty.newVal(name) else KotlinProperty.newVar(name)
            builder =
                builder
                    .withDataType(dataType)
            if (modifier != null) builder = builder.withModifier(modifier)
            if (annotation != null) builder = builder.withAnnotation(annotation)
            if (initializationString != null) builder = builder.withInitializationString(initializationString)

            val p = builder.build()
            val expectedModifier = listOfNotNull(modifier).toTypedArray()
            val expectedAnnotation = listOfNotNull(annotation).toTypedArray()

            val actual = NotNullFieldPlugin.toNonnullProperty(p)

            assertAll(
                { assertEquals(type, actual.type) },
                { assertEquals(name, actual.name) },
                { assertEquals(Optional.ofNullable(expectedDataType), actual.dataType) },
                { assertArrayEquals(expectedModifier, actual.modifiers.toTypedArray()) },
                { assertArrayEquals(expectedAnnotation, actual.annotations.toTypedArray()) },
                { assertEquals(Optional.ofNullable(expectedInitializationString), actual.initializationString) },
            )
        }
    }

    @Nested
    inner class FindNotNullPropertyNamesTest {
        @Test
        fun 全てのカラムがnullableだったら空のセットが返る() {
            val introspectedTable = IntrospectedTableKotlinImpl()
            listOf(
                {
                    val column = IntrospectedColumn()
                    column.javaProperty = "memberId"
                    column.isNullable = true
                    column
                },
                {
                    val column = IntrospectedColumn()
                    column.javaProperty = "name"
                    column.isNullable = true
                    column
                },
            ).forEach { introspectedTable.addColumn(it.invoke()) }

            assertTrue(NotNullFieldPlugin.findNotNullPropertyNames(introspectedTable).isEmpty())
        }

        @Test
        fun `一つでもnot nullのカラムがあればnot nullのカラム名を返す`() {
            val introspectedTable1 = IntrospectedTableKotlinImpl()
            listOf(
                {
                    val column = IntrospectedColumn()
                    column.javaProperty = "memberId"
                    column.isNullable = false
                    column
                },
                {
                    val column = IntrospectedColumn()
                    column.javaProperty = "name"
                    column.isNullable = true
                    column
                },
            ).forEach { introspectedTable1.addColumn(it.invoke()) }
            val introspectedTable2 = IntrospectedTableKotlinImpl()
            listOf(
                {
                    val column = IntrospectedColumn()
                    column.javaProperty = "memberId"
                    column.isNullable = false
                    column
                },
                {
                    val column = IntrospectedColumn()
                    column.javaProperty = "name"
                    column.isNullable = false
                    column
                },
            ).forEach { introspectedTable2.addColumn(it.invoke()) }

            assertIterableEquals(setOf("memberId"), NotNullFieldPlugin.findNotNullPropertyNames(introspectedTable1))
            assertIterableEquals(
                setOf("memberId", "name"),
                NotNullFieldPlugin.findNotNullPropertyNames(introspectedTable2),
            )
        }
    }
}
