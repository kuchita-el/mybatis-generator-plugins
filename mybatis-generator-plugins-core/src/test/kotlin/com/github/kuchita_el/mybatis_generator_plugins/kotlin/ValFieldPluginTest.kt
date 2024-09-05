package com.github.kuchita_el.mybatis_generator_plugins.kotlin

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mybatis.generator.api.dom.kotlin.KotlinModifier
import org.mybatis.generator.api.dom.kotlin.KotlinProperty
import java.util.*


class ValFieldPluginTest {

    @Nested
    inner class ToValPropertyTest {
        @ParameterizedTest
        @CsvSource(
            "field1, String?, PRIVATE, @Getter, defaultString",
            "field1, String?, NULL, NULL, NULL",
            nullValues = ["NULL"]
        )
        fun `varをvalに変換する`(name: String, dataType: String, modifier: KotlinModifier?, annotation: String?, initializationString: String?) {
            var builder = KotlinProperty.newVar(name)
                .withDataType(dataType)
            if (modifier != null) builder = builder.withModifier(modifier)
            if (annotation != null) builder = builder.withAnnotation(annotation)
            if (initializationString != null) builder = builder.withInitializationString(initializationString)

            val p = builder.build()
            val expectedModifier = listOfNotNull(modifier).toTypedArray()
            val expectedAnnotation = listOfNotNull(annotation).toTypedArray()

            val actual = ValFieldPlugin.toValProperty(p)

            assertAll(
                { assertEquals(KotlinProperty.Type.VAL, actual.type) },
                { assertEquals(name, actual.name) },
                { assertEquals(Optional.ofNullable(dataType), actual.dataType) },
                { assertArrayEquals(expectedModifier, actual.modifiers.toTypedArray()) },
                { assertArrayEquals(expectedAnnotation, actual.annotations.toTypedArray()) },
                { assertEquals(Optional.ofNullable(initializationString), actual.initializationString) }
            )
        }
    }
}
