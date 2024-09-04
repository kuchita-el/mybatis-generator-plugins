package com.github.dkurata38.mybatis_generator_plugins.kotlin

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.mybatis.generator.api.IntrospectedColumn
import org.mybatis.generator.api.dom.kotlin.KotlinProperty
import org.mybatis.generator.runtime.kotlin.IntrospectedTableKotlinImpl
import java.util.stream.Stream

class NotNullFieldPluginTest {
    @Nested
    inner class ToNonnullPropertyTest {
        @TestFactory
        fun `プロパティのデータ型がString型になる`(): Stream<DynamicTest> = DynamicTest.stream(
            listOf(
                KotlinProperty.newVar("name")
                    .withDataType("String")
                    .build(),
                KotlinProperty.newVar("name")
                    .withDataType("String?")
                    .withInitializationString("null")
                    .build()
            ).stream(),
            { "元のデータ型が${it.dataType.get()}の場合" },
            {
                val actual = NotNullFieldPlugin.toNonnullProperty(it)
                assertEquals("String", actual.dataType.get())
            }
        )
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
                }
            )
                .forEach { introspectedTable.addColumn(it.invoke()) }

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
                }
            )
                .forEach { introspectedTable1.addColumn(it.invoke()) }
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
                }
            )
                .forEach { introspectedTable2.addColumn(it.invoke()) }

            assertIterableEquals(setOf("memberId"), NotNullFieldPlugin.findNotNullPropertyNames(introspectedTable1))
            assertIterableEquals(
                setOf("memberId", "name"),
                NotNullFieldPlugin.findNotNullPropertyNames(introspectedTable2)
            )
        }
    }
}

