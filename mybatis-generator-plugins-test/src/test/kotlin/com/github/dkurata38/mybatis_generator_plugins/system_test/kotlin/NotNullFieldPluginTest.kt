package com.github.dkurata38.mybatis_generator_plugins.system_test.kotlin

import com.github.dkurata38.mybatis_generator_plugins.mybatis_generator_plugins_test.mybatis3_kotlin.Member
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.fail
import kotlin.reflect.full.memberProperties

class NotNullFieldPluginTest {
    @Test
    fun `notnull制約が課されたカラムに対応するフィールドはnonnull型である。`() {
        val constructorParameter =
            Member::class.constructors.toList()[0].parameters.find { it.name == "memberId" } ?: fail("")
        val memberProperty = Member::class.memberProperties.toList().find { it.name == "memberId" } ?: fail("")

        assertAll(
            { assertFalse(constructorParameter.isOptional) },
            { assertFalse(constructorParameter.type.isMarkedNullable) },
            { assertFalse(memberProperty.returnType.isMarkedNullable) }
        )
    }

    @Test
    fun `notnull制約が課されていないカラムに対応するフィールドはnullable型である。`() {
        val constructorParameter =
            Member::class.constructors.toList()[0].parameters.find { it.name == "name" } ?: fail("")
        val memberProperty = Member::class.memberProperties.toList().find { it.name == "name" } ?: fail("")

        assertAll(
            { assertTrue(constructorParameter.isOptional) },
            { assertTrue(constructorParameter.type.isMarkedNullable) },
            { assertTrue(memberProperty.returnType.isMarkedNullable) }
        )
    }
}
