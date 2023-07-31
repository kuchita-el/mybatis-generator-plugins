package com.github.dkurata38.mybatis_generator_plugins.system_test.kotlin

import com.github.dkurata38.mybatis_generator_plugins.mybatis_generator_plugins_test.mybatis3_kotlin.member.Member
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import kotlin.reflect.full.memberProperties

class NotNullFieldPluginTest {
    @Test
    fun `notnull制約が課されたカラムに対応するフィールドはnonnull型である。` () {
        val constructorParameter = Member::class.constructors.toList()[0].parameters.find { it.name == "memberId" }?: fail("")
        val memberProperty = Member::class.memberProperties.toList().find { it.name == "memberId" }?: fail("")
        constructorParameter.run {
            assertFalse(this.isOptional)
            assertFalse(this.type.isMarkedNullable)
        }
        memberProperty.run {
            assertFalse(this.returnType.isMarkedNullable)
        }
    }

    @Test
    fun `notnull制約が課されていないカラムに対応するフィールドはnullable型である。` () {
        val constructorParameter = Member::class.constructors.toList()[0].parameters.find { it.name == "name" }?: fail("")
        val memberProperty = Member::class.memberProperties.toList().find { it.name == "name" }?: fail("")
        constructorParameter.run {
            assertTrue(this.isOptional)
            assertTrue(this.type.isMarkedNullable)
        }
        memberProperty.run {
            assertTrue(this.returnType.isMarkedNullable)
        }
    }
}