package com.github.dkurata38.mybatis_generator_plugins.system_test.kotlin

import com.github.dkurata38.mybatis_generator_plugins.mybatis_generator_plugins_test.mybatis3_kotlin.Member
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import kotlin.reflect.full.memberProperties

class ValFieldPluginTest {
    @Test
    fun フィールドの修飾子がvalになる() {
        val memberProperty = Member::class.memberProperties.toList().find { it.name == "memberId" } ?: fail("")

        assertTrue(memberProperty.isFinal)

    }
}
